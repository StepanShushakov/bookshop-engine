package com.example.mybookshopapp.data.dto.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * @author karl
 */

@Getter
@Setter
public class RateBookRequestDto {
    private int bookId;
    private int value;
}
