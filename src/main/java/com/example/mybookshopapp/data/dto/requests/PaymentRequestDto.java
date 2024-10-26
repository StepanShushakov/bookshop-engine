package com.example.mybookshopapp.data.dto.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * @author karl
 */

@Getter
@Setter
public class PaymentRequestDto {

    private String hash;
    private Integer sum;
    private Long time;
}
