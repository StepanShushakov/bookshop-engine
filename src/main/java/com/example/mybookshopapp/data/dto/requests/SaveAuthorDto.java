package com.example.mybookshopapp.data.dto.requests;

import com.example.mybookshopapp.data.model.author.AuthorEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author karl
 */

@Getter
@Setter
public class SaveAuthorDto {

    private int id;
    private String slug;
    private String photo;
    private String name;
    private String description;

    public SaveAuthorDto() {

    }

    public SaveAuthorDto(AuthorEntity authorEntity) {
        if (authorEntity == null) {
            return;
        }
        this.id = authorEntity.getId();
        this.slug = authorEntity.getSlug();
        this.photo = authorEntity.getPhoto();
        this.name = authorEntity.getName();
        this.description = authorEntity.getDescription();
    }
}
