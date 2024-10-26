package com.example.mybookshopapp.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author karl
 */

@Getter
@Setter
public class BooksPageDto {

    private Integer count;
    private List<BookDto> books;

    public BooksPageDto(List<BookDto> books) {
        this.count = books.size();
        this.books = books;
    }
}
