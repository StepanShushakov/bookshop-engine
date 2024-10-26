package com.example.mybookshopapp.data.dto;

import com.example.mybookshopapp.data.model.book.BookEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author karl
 */

@Getter
@Setter
public class BookSlugDto extends BookDto{

    private List<AuthorDto> autorList = new ArrayList<>();
    private List<TagNameDto> tagList = new ArrayList<>();
    @JsonBackReference
    private List<BookFileDto> bookFiles;

    public BookSlugDto(BookEntity bookEntity) {
        super(bookEntity);
    }
}
