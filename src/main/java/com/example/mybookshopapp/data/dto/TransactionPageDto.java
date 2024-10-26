package com.example.mybookshopapp.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author karl
 */

@Getter
@Setter
public class TransactionPageDto {
    private Integer count;
    private List<TransactionDto> transactions;

    public TransactionPageDto(List<TransactionDto> transactionDtos) {
        this.count = transactionDtos.size();
        this.transactions = transactionDtos;
    }
}
