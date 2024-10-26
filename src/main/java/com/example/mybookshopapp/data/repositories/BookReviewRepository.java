package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.model.book.review.BookReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author karl
 */

public interface BookReviewRepository extends JpaRepository<BookReviewEntity, Integer> {
    List<BookReviewEntity> findBookReviewEntitiesByBook(BookEntity bookId);

    @Query(nativeQuery = true, value = "select * from book_review where id = :id")
    BookReviewEntity findBookReviewEntityOnId(@Param("id")int reviewId);
}
