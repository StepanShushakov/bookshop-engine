package com.example.mybookshopapp.security.repositories;

import com.example.mybookshopapp.data.model.user.JWTBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author karl
 */

@Repository
public interface JWTBlacklistRepository extends JpaRepository<JWTBlacklistEntity, Integer> {
    long deleteByToken(@NonNull String token);

    List<JWTBlacklistEntity> findByTokenEquals(String token);
}
