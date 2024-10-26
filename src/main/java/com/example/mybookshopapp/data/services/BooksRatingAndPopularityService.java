package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.model.book.BookRatingEntity;
import com.example.mybookshopapp.data.model.book.review.BookReviewLikeEntity;
import com.example.mybookshopapp.data.model.user.UserEntity;
import com.example.mybookshopapp.data.repositories.BookRatingRepository;
import com.example.mybookshopapp.data.repositories.BookRepository;
import com.example.mybookshopapp.data.repositories.BookReviewLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;

/**
 * @author karl
 */

@Service
public class BooksRatingAndPopularityService {

    private final BookRepository bookRepository;
    private final BookReviewLikeRepository bookReviewLikeRepository;
    private final BookRatingRepository bookRatingRepository;

    @Autowired
    public BooksRatingAndPopularityService(BookRepository bookRepository,
                                           BookReviewLikeRepository bookReviewLikeRepository,
                                           BookRatingRepository bookRatingRepository) {
        this.bookRepository = bookRepository;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
        this.bookRatingRepository = bookRatingRepository;
    }

    public Page<BookEntity> getPageOfPopularBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findPopular(LocalDateTime.now().minusWeeks(1), nextPage);
    }

    public void setReviewLike(UserEntity user, int reviewId, short value) {
        BookReviewLikeEntity reviewLikeEntity = bookReviewLikeRepository.findByUserAndReviewId(user, reviewId);
        if (reviewLikeEntity == null) {
            reviewLikeEntity = new BookReviewLikeEntity();
        }
        reviewLikeEntity.setUser(user);
        reviewLikeEntity.setReviewId(reviewId);
        reviewLikeEntity.setTime(LocalDateTime.now());
        reviewLikeEntity.setValue(value);
        bookReviewLikeRepository.save(reviewLikeEntity);

    }

    public void setBookRate(UserEntity userEntity, int bookId, int value) {
        BookEntity bookEntity = (BookEntity) bookRepository.findBookEntityById(bookId);
        BookRatingEntity bookRatingEntity = bookRatingRepository.findByBookAndUser(bookEntity, userEntity);
        if (bookRatingEntity == null) {
            bookRatingEntity = new BookRatingEntity();
        }
        bookRatingEntity.setUser(userEntity);
        bookRatingEntity.setBook(bookEntity);
        bookRatingEntity.setValue(value);
        bookRatingRepository.save(bookRatingEntity);
    }

    public int getUserRating(BookEntity bookEntity, int userId) {
        BookRatingEntity bookRatingEntity = bookRatingRepository.findByBookAndUserId(bookEntity, userId);
        if (bookRatingEntity != null) {
            return bookRatingEntity.getValue();
        }
        return 0;
    }
}
