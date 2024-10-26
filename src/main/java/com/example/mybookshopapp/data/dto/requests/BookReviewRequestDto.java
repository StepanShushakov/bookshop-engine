package com.example.mybookshopapp.data.dto.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * @author karl
 */

@Getter
@Setter
public class BookReviewRequestDto {

    private int bookId;
    private String text;
}
