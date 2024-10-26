package com.example.mybookshopapp.security.services;

import com.example.mybookshopapp.data.dto.UserDto;
import com.example.mybookshopapp.data.dto.cookie.BookIds;
import com.example.mybookshopapp.data.dto.requests.ProfileChangeRequestDto;
import com.example.mybookshopapp.data.dto.requests.SaveUserDto;
import com.example.mybookshopapp.data.model.enums.Provider;
import com.example.mybookshopapp.data.model.user.ProfileChangeEntity;
import com.example.mybookshopapp.data.model.user.UserEntity;
import com.example.mybookshopapp.data.repositories.BookRepository;
import com.example.mybookshopapp.data.repositories.ProfileChangeRepository;
import com.example.mybookshopapp.data.services.Book2UserService;
import com.example.mybookshopapp.errs.UserExistsException;
import com.example.mybookshopapp.errs.UserNotPermissionException;
import com.example.mybookshopapp.security.*;
import com.example.mybookshopapp.security.jwt.JWTUtil;
import com.example.mybookshopapp.security.repositories.BookstoreUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author karl
 */

@Service
public class BookstoreUserRegister {

    private final BookstoreUserRepository bookstoreUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BookstoreUserDetailService bookstoreUserDetailService;
    private final JWTUtil jwtUtil;
    private final ProfileChangeRepository profileChangeRepository;
    private final BookRepository bookRepository;
    private final Book2UserService book2UserService;

    @Autowired
    public BookstoreUserRegister(BookstoreUserRepository bookstoreUserRepository,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 BookstoreUserDetailService bookstoreUserDetailService,
                                 JWTUtil jwtUtil, ProfileChangeRepository profileChangeRepository, BookRepository bookRepository,
                                 Book2UserService book2UserService) {
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.bookstoreUserDetailService = bookstoreUserDetailService;
        this.jwtUtil = jwtUtil;
        this.profileChangeRepository = profileChangeRepository;
        this.bookRepository = bookRepository;
        this.book2UserService = book2UserService;
    }

    public UserEntity registerNewUser(RegistrationForm registrationForm, BookIds slugs) throws UserExistsException {

        UserEntity userByEmail = null;
        if (registrationForm.getEmail() != null) {
            userByEmail = bookstoreUserRepository.findUserEntitiesByEmail(registrationForm.getEmail());
        }

        UserEntity userByPhone = null;
        if (registrationForm.getPhone() != null) {
             userByPhone = bookstoreUserRepository.findUserEntityByPhone(registrationForm.getPhone());
        }

        if (userByEmail == null && userByPhone == null) {
            UserEntity userEntity = new UserEntity();
            userEntity.setName(registrationForm.getName());
            userEntity.setEmail(registrationForm.getEmail());
            userEntity.setPhone(registrationForm.getPhone());
            userEntity.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            userEntity.setBalance(0);
            userEntity.setRegTime(LocalDateTime.now());
            userEntity.setHash(UUID.randomUUID().toString());
            userEntity.setProvider(Provider.LOCAL);
            bookstoreUserRepository.save(userEntity);
            saveCookieSlugsInDataBase(userEntity.getId(), slugs);
            return userEntity;
        } else if (userByPhone != null) {
            throw new UserExistsException("the user with the specified phone is already registered");
        }
        throw new UserExistsException("the user with the specified email is already registered");
    }

    private void saveCookieSlugsInDataBase(int userId, BookIds bookIds) {
        List<Integer> cartIds = bookIds.getCartIds();
        if (cartIds != null) {
            handleSaveCookieSlug(userId,
                    cartIds,
                    "CART",
                    LocalDateTime.now());
        }
        List<Integer> postponedIds= bookIds.getPostponedIds();
        if (postponedIds != null) {
            handleSaveCookieSlug(userId,
                    postponedIds,
                    "KEPT",
                    LocalDateTime.now());
        }

    }

    private void handleSaveCookieSlug(int userId, List<Integer> bookId, String typeCode, LocalDateTime time) {
        bookRepository.findBookEntitiesByIdIn(bookId).forEach(bookEntity ->
                book2UserService.addBook2UserLink(userId, bookEntity.getId(), typeCode, time));
    }

    public ContactConfirmationResponse login(ContactConfirmationPayload payload) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(),
                        payload.getCode()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    public ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(),
                payload.getCode()));
        BookstoreUserDetails userDetails =
                (BookstoreUserDetails) bookstoreUserDetailService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public ContactConfirmationResponse jwtLoginByPhoneNumber(ContactConfirmationPayload payload, BookIds slugs) throws UserExistsException {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setPhone(payload.getContact());
        registrationForm.setPass(payload.getCode());
        registerNewUser(registrationForm, slugs);
        UserDetails userDetails = bookstoreUserDetailService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public Object getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOidcUser) {
            return bookstoreUserRepository.findUserEntitiesByEmail(
                    ((DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                            .getEmail());
        } else if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof BookstoreUserDetails userDetails) {
            return userDetails.getUserEntity();
//            return bookstoreUserRepository.findUserEntityById(userDetails.getUserEntity().getId());
        } else {
            return null;
        }
    }

    public UserDto getCurrentUserDto() {
        Object currentUser = getCurrentUser();
        if (currentUser instanceof UserEntity userEntity) {
            return new UserDto(userEntity);
        } else {
            return new UserDto();
        }
    }
    public void checkAuthorization(String token) throws UserNotPermissionException {
        if (token == null) {
            throw new UserNotPermissionException("user not authorized");
        }
    }

    public ProfileChangeEntity createProfileChangeRecord(ProfileChangeRequestDto requestDto) {
        ProfileChangeEntity profileChangeEntity = new ProfileChangeEntity();
        profileChangeEntity.setUser((UserEntity) getCurrentUser());
        profileChangeEntity.setName(requestDto.getName());
        profileChangeEntity.setPhone(requestDto.getPhone());
        profileChangeEntity.setEmail(requestDto.getMail());
        profileChangeEntity.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        profileChangeEntity.setToken(UUID.randomUUID().toString());
        profileChangeRepository.save(profileChangeEntity);
        return profileChangeEntity;
    }

    public void updateUserOnRequest(ProfileChangeEntity profileChangeRequest) {
        UserEntity userEntity = profileChangeRequest.getUser();
        userEntity.setName(profileChangeRequest.getName());
        userEntity.setEmail(profileChangeRequest.getEmail());
        userEntity.setPhone(profileChangeRequest.getPhone());
        userEntity.setPassword(profileChangeRequest.getPassword());
        bookstoreUserRepository.save(userEntity);
        profileChangeRepository.delete(profileChangeRequest);
    }

    public List<UserEntity> getUserEntitiesOnEmail(String email) {
        return bookstoreUserRepository.findUserEntitiesByEmailEquals(email);
    }

    public UserEntity getUserEntityByPhone(String phone) {
        return bookstoreUserRepository.findUserEntityByPhone(phone);
    }

    public UserEntity getUserEntityById(Integer id) {
        return bookstoreUserRepository.findUserEntityById(id);
    }

    public void save(SaveUserDto saveUserDto) {
        UserEntity userEntity = bookstoreUserRepository.findUserEntityById(saveUserDto.getId());
        if (!saveUserDto.getPassword().isEmpty()) {
            userEntity.setPassword(passwordEncoder.encode(saveUserDto.getPassword()));
        }
        userEntity.setRole(saveUserDto.getRole());
        bookstoreUserRepository.save(userEntity);
    }
}
