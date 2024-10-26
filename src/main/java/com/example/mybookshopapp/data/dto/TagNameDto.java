package com.example.mybookshopapp.data.dto;

import com.example.mybookshopapp.data.model.book.TagEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author karl
 */

@Getter
@Setter
public class TagNameDto {
    private String name;

    public TagNameDto(String name) {
        this.name = name;
    }

    public TagNameDto(TagEntity tagEntity) {
        this.name = tagEntity.getName();
    }
}
