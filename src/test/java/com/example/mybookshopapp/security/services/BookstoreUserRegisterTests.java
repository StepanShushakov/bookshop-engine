package com.example.mybookshopapp.security.services;

import com.example.mybookshopapp.data.dto.cookie.BookIds;
import com.example.mybookshopapp.data.model.user.UserEntity;
import com.example.mybookshopapp.errs.UserExistsException;
import com.example.mybookshopapp.security.BookstoreUserDetails;
import com.example.mybookshopapp.security.ContactConfirmationPayload;
import com.example.mybookshopapp.security.ContactConfirmationResponse;
import com.example.mybookshopapp.security.RegistrationForm;
import com.example.mybookshopapp.security.jwt.JWTUtil;
import com.example.mybookshopapp.security.repositories.BookstoreUserRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author karl
 */

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookstoreUserRegisterTests {

    private final BookstoreUserRegister userRegister;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private RegistrationForm registrationForm;
    private ContactConfirmationPayload payload;

    @MockBean
    private BookstoreUserRepository bookstoreUserRepositoryMock;

    @MockBean
    private BookstoreUserDetailService userDetailServiceMock;

    @Autowired
    BookstoreUserRegisterTests(BookstoreUserRegister userRegister, PasswordEncoder passwordEncoder, JWTUtil jwtUtil) {
        this.userRegister = userRegister;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @BeforeEach
    void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@gmail.org");
        registrationForm.setName("Tester");
        registrationForm.setPass("iddqd");
        registrationForm.setPhone("9031232323");

        payload = new ContactConfirmationPayload();
//        payload.setContact("step.sv@mail.ru");
//        payload.setCode("123456");
        payload.setContact(registrationForm.getEmail());
        payload.setCode(registrationForm.getPass());
    }

    @AfterEach
    void tearDown() {
        registrationForm = null;
        payload = null;
    }

    @Test
    void registerNewUser() throws UserExistsException {
        BookIds slugs = new BookIds();
        UserEntity userEntity = userRegister.registerNewUser(registrationForm, slugs);
        assertNotNull(userEntity);
        assertTrue(passwordEncoder.matches(registrationForm.getPass(), userEntity.getPassword()));
        assertTrue(CoreMatchers.is(userEntity.getPhone()).matches(registrationForm.getPhone()));
        assertTrue(CoreMatchers.is(userEntity.getEmail()).matches(registrationForm.getEmail()));
        assertTrue(CoreMatchers.is(userEntity.getName()).matches(registrationForm.getName()));

        Mockito.verify(bookstoreUserRepositoryMock,Mockito.times(1))
                .save(Mockito.any(UserEntity.class));
    }

    @Test
    void registerNewUserFail() throws UserExistsException {
        Mockito.doReturn(new UserEntity())
                .when(bookstoreUserRepositoryMock)
                .findUserEntitiesByEmail(registrationForm.getEmail());

        try {
            BookIds slugs = new BookIds();
            UserEntity userEntity = userRegister.registerNewUser(registrationForm, slugs);
        } catch (Exception exception) {
            assertEquals(exception.getClass(), UserExistsException.class);
        }


    }

    @Test
    void jwtLogin() throws UserExistsException {
        BookIds slugs = new BookIds();
        Mockito.doReturn(new BookstoreUserDetails(userRegister.registerNewUser(registrationForm, slugs)))
                .when(userDetailServiceMock)
                .loadUserByUsername(payload.getContact());

        ContactConfirmationResponse response = userRegister.jwtLogin(payload);
        String token = response.getResult();
        Logger.getLogger(this.getClass().getSimpleName()).info("token is: " + token);
        Logger.getLogger(this.getClass().getSimpleName()).info("expiration date: " + jwtUtil.extractExpiration(token));
        assertNotNull(response.getResult());
//        assertFalse(jwtUtil.isTokenExpired(token));
//        assertFalse(blacklistService.tokenAtBlacklist(token));
    }
}