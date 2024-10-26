package com.example.mybookshopapp.security.services;

import com.example.mybookshopapp.data.model.enums.Provider;
import com.example.mybookshopapp.data.model.user.UserEntity;
import com.example.mybookshopapp.security.BookstoreUserDetails;
import com.example.mybookshopapp.security.PhoneNumberUserDetails;
import com.example.mybookshopapp.security.repositories.BookstoreUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author karl
 */

@Service
public class BookstoreUserDetailService  implements UserDetailsService {

    private final BookstoreUserRepository bookstoreUserRepository;

    @Autowired
    public BookstoreUserDetailService(BookstoreUserRepository bookstoreUserRepository) {
        this.bookstoreUserRepository = bookstoreUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        UserEntity userEntity = bookstoreUserRepository.findUserEntitiesByEmail(s);
        if (userEntity != null) {
            return new BookstoreUserDetails(userEntity);
        }

        userEntity = bookstoreUserRepository.findUserEntityByPhone(s);
        if (userEntity != null) {
            return new PhoneNumberUserDetails(userEntity);
        } else {
            throw new UsernameNotFoundException("user not found doh!");
        }
    }

    public void processOAuthPostLogin(DefaultOidcUser oauthUser) {

        UserEntity existUser = bookstoreUserRepository.getUserByUserEmail(oauthUser.getEmail());

        if (existUser == null) {
            UserEntity newUser = new UserEntity();
            newUser.setName(oauthUser.getAttribute("name"));
            newUser.setEmail(oauthUser.getEmail());
            newUser.setBalance(0);
            newUser.setRegTime(LocalDateTime.now());
            newUser.setHash(UUID.randomUUID().toString());
            newUser.setProvider(Provider.GOOGLE);
//            newUser.SetEnabled(true);

            bookstoreUserRepository.save(newUser);
        }
    }
}
