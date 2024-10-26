package com.example.mybookshopapp.security;

import lombok.Getter;
import lombok.Setter;

/**
 * @author karl
 */

@Getter
@Setter
public class RegistrationForm {
    private String name;
    private String email;
    private String phone;
    private String pass;
}
