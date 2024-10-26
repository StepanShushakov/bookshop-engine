package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.book.review.BookReviewLikeEntity;
import com.example.mybookshopapp.data.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author karl
 */

public interface BookReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer> {
    BookReviewLikeEntity findByUserAndReviewId(UserEntity user, int reviewId);

    @Query(nativeQuery = true, value = "select" +
            "   count(value) " +
            "from book_review_like " +
            "where" +
            "   review_id = :review_id" +
            "   and value = 1")
    int getLikesCountOnReviewId(@Param("review_id") int review_id);

    @Query(nativeQuery = true, value = "select" +
            "   count(value) " +
            "from book_review_like " +
            "where" +
            "   review_id = :review_id" +
            "   and value = -1")
    int getDislikesCountOnReviewId(@Param("review_id") int review_id);
}
