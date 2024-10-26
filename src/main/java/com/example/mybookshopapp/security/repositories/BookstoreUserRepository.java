package com.example.mybookshopapp.security.repositories;

import com.example.mybookshopapp.data.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author karl
 */

@Repository
public interface BookstoreUserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findUserEntitiesByEmail(String email);

    List<UserEntity> findUserEntitiesByEmailEquals(String email);

    UserEntity findUserEntityByPhone(String phone);

    @Query(nativeQuery = true, value = "SELECT u.* FROM users u WHERE u.email = :email")
    UserEntity getUserByUserEmail(@Param("email") String email);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE from users where email = :email")
    void deleteByEmail(@Param("email") String email);

    UserEntity findUserEntityById(int id);

    UserEntity findUserEntityByHash(String userHash);
}
