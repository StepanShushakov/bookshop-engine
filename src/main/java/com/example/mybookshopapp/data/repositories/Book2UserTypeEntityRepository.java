package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.book.links.Book2UserTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author karl
 */

@Repository
public interface Book2UserTypeEntityRepository extends JpaRepository<Book2UserTypeEntity, Integer> {
    Book2UserTypeEntity findByCode(String code);
}