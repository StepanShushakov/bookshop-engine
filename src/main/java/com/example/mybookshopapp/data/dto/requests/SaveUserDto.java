package com.example.mybookshopapp.data.dto.requests;

import com.example.mybookshopapp.data.model.user.UserEntity;
import com.example.mybookshopapp.security.enums.Role;
import lombok.Getter;
import lombok.Setter;

/**
 * @author karl
 */

@Getter
@Setter
public class SaveUserDto {

    private int id;
    private String name;
    private String password;
    private Role role;

    public SaveUserDto(UserEntity userEntity) {
        if (userEntity == null) {
            return;
        }
        this.id = userEntity.getId();
        this.name = userEntity.getName();
        this.password = userEntity.getPassword();
        this.role = userEntity.getRole();
    }
}
