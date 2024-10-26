package com.example.mybookshopapp.data.dto;

import com.example.mybookshopapp.data.model.book.BookFileEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * @author karl
 */

public class BookFileDto {

    private int id;
    private String hash;
    private String path;
    @JsonManagedReference
    private BookDto bookDto;
    private String bookFileExtensionString;

    public BookFileDto(BookFileEntity bookFileEntity) {
        this.id = bookFileEntity.getId();
        this.hash = bookFileEntity.getHash();
        this.path = bookFileEntity.getPath();
//        this.bookDto = ;
    }

    public String getBookFileExtensionString() {
        return bookFileExtensionString;
    }

    public void setBookFileExtensionString(String bookFileExtensionString) {
        this.bookFileExtensionString = bookFileExtensionString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public BookDto getBookDto() {
        return bookDto;
    }

    public void setBookDto(BookDto bookDto) {
        this.bookDto = bookDto;
    }
}
