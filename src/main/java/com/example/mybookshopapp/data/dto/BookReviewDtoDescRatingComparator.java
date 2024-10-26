package com.example.mybookshopapp.data.dto;

import java.util.Comparator;

/**
 * @author karl
 */

public class BookReviewDtoDescRatingComparator implements Comparator<BookReviewDto> {
    @Override
    public int compare(BookReviewDto o1, BookReviewDto o2) {
        return Integer.compare(o2.getRating(), o1.getRating());
    }
}
