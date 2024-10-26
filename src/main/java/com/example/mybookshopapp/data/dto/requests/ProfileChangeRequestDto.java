package com.example.mybookshopapp.data.dto.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * @author karl
 */

@Getter
@Setter
public class ProfileChangeRequestDto {

    private String name;
    private String mail;
    private String phone;
    private String password;
    private String passwordReply;

}
