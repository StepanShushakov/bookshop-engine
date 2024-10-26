package com.example.mybookshopapp.security;

import com.example.mybookshopapp.data.model.user.UserEntity;

/**
 * @author karl
 */

public class PhoneNumberUserDetails extends BookstoreUserDetails {
    public PhoneNumberUserDetails(UserEntity userEntity) {
        super(userEntity);
    }

    @Override
    public String getUsername() {
        return getUserEntity().getPhone();
    }
}
