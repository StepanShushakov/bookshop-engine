package com.example.mybookshopapp.security;

import lombok.Getter;
import lombok.Setter;

/**
 * @author karl
 */

@Getter
@Setter
public class ContactConfirmationPayload {

    private String contact;
    private String code;
}
