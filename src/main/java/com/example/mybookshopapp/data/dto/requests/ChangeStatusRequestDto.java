package com.example.mybookshopapp.data.dto.requests;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author karl
 */

@Data
public class ChangeStatusRequestDto {

    private String status;
    private List<Integer> booksIds = new ArrayList<>();
}
