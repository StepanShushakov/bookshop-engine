package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.model.book.BookRatingEntity;
import com.example.mybookshopapp.data.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author karl
 */

public interface BookRatingRepository extends JpaRepository<BookRatingEntity, Integer> {
    BookRatingEntity findByBookAndUser(BookEntity book, UserEntity user);

    BookRatingEntity findByBookAndUserId(BookEntity book, int userId);
}
