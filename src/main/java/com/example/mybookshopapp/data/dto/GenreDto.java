package com.example.mybookshopapp.data.dto;

import com.example.mybookshopapp.data.model.genre.GenreEntity;

import java.util.List;

/**
 * @author karl
 */

public class GenreDto {

    private Integer id;
    private String slug;
    private Integer parentId;
    private String name;
    private Integer booksCount;
    private List<GenreDto> children;

    public GenreDto(GenreEntity genreEntity) {
        this.id = genreEntity.getId();
        this.slug = genreEntity.getSlug();
        this.parentId = genreEntity.getParentId();
        this.name = genreEntity.getName();
        this.booksCount = genreEntity.getBooks().size();
//        this.children = ; //в конструкторе пока не будем задавать
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBooksCount() {
        return booksCount;
    }

    public void setBooksCount(Integer booksCount) {
        this.booksCount = booksCount;
    }

    public List<GenreDto> getChildren() {
        return children;
    }

    public void setChildren(List<GenreDto> children) {
        this.children = children;
    }
}
