package com.example.mybookshopapp.data.dto;

import com.example.mybookshopapp.data.model.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author karl
 */

@Getter
@Setter
public class UserDto {

    private int id;
    private int balance;
    private String name;
    private String email;
    private String phone;
    private String hash;

    public UserDto() {

    }
    public UserDto(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.balance = userEntity.getBalance();
        this.name = userEntity.getName();
        this.email = userEntity.getEmail();
        this.phone = userEntity.getPhone();
        this.hash = userEntity.getHash();
    }
}
