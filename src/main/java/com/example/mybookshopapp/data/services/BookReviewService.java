package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.model.book.review.BookReviewEntity;
import com.example.mybookshopapp.data.repositories.BookReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author karl
 */

@Service
public class BookReviewService {
    private final BookReviewRepository bookReviewRepository;

    @Autowired
    public BookReviewService(BookReviewRepository bookReviewRepository) {
        this.bookReviewRepository = bookReviewRepository;
    }

    public List<BookReviewEntity> getReviewEntitiesOnBook(BookEntity bookEntity) {
        return bookReviewRepository.findBookReviewEntitiesByBook(bookEntity);
    }

    public BookReviewEntity getReviewEntityOnId(int reviewId) {
        return bookReviewRepository.findBookReviewEntityOnId(reviewId);
    }
}
