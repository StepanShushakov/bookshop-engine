package com.example.mybookshopapp.data.dto;

import com.example.mybookshopapp.data.model.book.review.BookReviewEntity;
import com.example.mybookshopapp.data.repositories.BookReviewLikeRepository;
import com.example.mybookshopapp.data.repositories.BookReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author karl
 */

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookReviewDtoTest {

    private BookReviewDto bookReviewDto;
    @Autowired
    private BookReviewRepository bookReviewRepository;
    @Autowired
    private BookReviewLikeRepository bookReviewLikeRepository;

    @BeforeEach
    void setUp() {
        BookReviewEntity bookReviewEntity = bookReviewRepository.findBookReviewEntityOnId(1);

        bookReviewDto = new BookReviewDto(bookReviewEntity);
        bookReviewDto.setLikeCount(bookReviewLikeRepository.getLikesCountOnReviewId(bookReviewEntity.getId()));
        bookReviewDto.setDislikeCount(bookReviewLikeRepository.getDislikesCountOnReviewId(bookReviewEntity.getId()));
    }

    @AfterEach
    void tearDown() {
        bookReviewDto = null;
    }

    private void loggingBookReviewLikeDislikeRatingCounts(BookReviewDto bookReviewDto) {
        Logger.getLogger(this.getClass().getSimpleName()).info("like count: " + bookReviewDto.getLikeCount());
        Logger.getLogger(this.getClass().getSimpleName()).info("dislike count: " + bookReviewDto.getDislikeCount());
        Logger.getLogger(this.getClass().getSimpleName()).info("rating: " + bookReviewDto.getRating());
    }

    @Test
    void setLikeCount() {
        bookReviewDto.setLikeCount(4);
        loggingBookReviewLikeDislikeRatingCounts(bookReviewDto);
        assertEquals(bookReviewDto.getRating(), bookReviewDto.getLikeCount() - bookReviewDto.getDislikeCount());
    }

    @Test
    void setDislikeCount() {
        bookReviewDto.setDislikeCount(4);
        loggingBookReviewLikeDislikeRatingCounts(bookReviewDto);
        assertEquals(bookReviewDto.getRating(), bookReviewDto.getLikeCount() - bookReviewDto.getDislikeCount());
    }
}