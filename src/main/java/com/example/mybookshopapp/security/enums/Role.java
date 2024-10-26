package com.example.mybookshopapp.security.enums;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author karl
 */

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {

    ADMIN("ADMIN"),
    USER("USER");

    private final String vale;
    @Override
    public String getAuthority() {
        return vale;
    }
}
