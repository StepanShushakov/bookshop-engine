package com.example.mybookshopapp.data.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author karl
 */

@Getter
@Setter
public class RateBookReviewRequestDto {

    @JsonProperty("reviewid")
    private int reviewId;
    private short value;
}
