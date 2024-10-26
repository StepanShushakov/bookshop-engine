package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.book.review.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author karl
 */

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
}
