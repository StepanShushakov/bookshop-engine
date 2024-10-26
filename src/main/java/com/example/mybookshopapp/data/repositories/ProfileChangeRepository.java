package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.user.ProfileChangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author karl
 */

@Repository
public interface ProfileChangeRepository extends JpaRepository<ProfileChangeEntity, Integer> {
    ProfileChangeEntity findByToken(String confirmToken);
}
