package com.example.mybookshopapp.security.jwt;

import com.example.mybookshopapp.security.repositories.JWTBlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author karl
 */

@Service
public class JWTBlacklistService {

    JWTBlacklistRepository blacklistRepository;

    @Autowired
    public JWTBlacklistService(JWTBlacklistRepository blacklistRepository) {
        this.blacklistRepository = blacklistRepository;
    }

    public boolean tokenAtBlacklist(String token) {
        return blacklistRepository.findByTokenEquals(token).size() != 0;
    }

    public void deleteFromBlacklist(String token) {
        blacklistRepository.deleteByToken(token);
    }
}
