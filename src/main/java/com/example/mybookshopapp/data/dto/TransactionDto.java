package com.example.mybookshopapp.data.dto;

import com.example.mybookshopapp.data.model.payments.BalanceTransactionEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * @author karl
 */

@Getter
@Setter
public class TransactionDto {

    private Long time;
    private Integer value;
    private String description;
    private String formattedTime;

    public TransactionDto(BalanceTransactionEntity entity) {
        this.description = entity.getDescription();
        this.value = entity.getValue();
        this.time = ZonedDateTime.of(entity.getTime(), ZoneId.systemDefault()).toEpochSecond() * 1000;
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
        this.formattedTime = (entity.getTime().format(formatter)).replaceAll("г.,", "");
    }
}
