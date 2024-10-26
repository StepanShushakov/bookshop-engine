package com.example.mybookshopapp.security.services;

import com.example.mybookshopapp.data.model.user.JWTBlacklistEntity;
import com.example.mybookshopapp.security.repositories.JWTBlacklistRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author karl
 */

@Service
public class CustomerLogoutHandler implements LogoutHandler {
    private final JWTBlacklistRepository jWTBlacklistRepository;

    public CustomerLogoutHandler(JWTBlacklistRepository jWTBlacklistRepository) {
        this.jWTBlacklistRepository = jWTBlacklistRepository;
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        for (Cookie cookie: httpServletRequest.getCookies()) {
            if (cookie.getName().equals("token")) {
                addTokenToBlacklist(cookie.getValue());
            }
        }
    }

    private void addTokenToBlacklist(String value) {
        JWTBlacklistEntity blacklistEntity = new JWTBlacklistEntity();
        blacklistEntity.setToken(value);
        jWTBlacklistRepository.save(blacklistEntity);
    }
}
