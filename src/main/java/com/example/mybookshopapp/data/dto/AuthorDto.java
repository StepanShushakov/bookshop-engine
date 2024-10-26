package com.example.mybookshopapp.data.dto;

import com.example.mybookshopapp.data.model.author.AuthorEntity;

/**
 * @author karl
 */

public class AuthorDto {

    private Integer id;
    private String descriptionPreview;
    private String descriptionSecondFull;
    private String name;
    private String photo;
    private String slug;

    public AuthorDto(AuthorEntity authorEntity) {
        this.id = authorEntity.getId();
        String description = authorEntity.getDescription();
        if (description!=null && description.length() > 417) {
            this.descriptionPreview = description.substring(0,417);
            this.descriptionSecondFull = description.substring(418);
        } else if (description!=null) {
            this.descriptionPreview = description;
        }
        this.name = authorEntity.getName();
        this.photo = authorEntity.getPhoto();
        this.slug = authorEntity.getSlug();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescriptionPreview() {
        return descriptionPreview;
    }

    public void setDescriptionPreview(String description) {
        this.descriptionPreview = description;
    }

    public String getDescriptionSecondFull() {
        return descriptionSecondFull;
    }

    public void setDescriptionSecondFull(String description) {
        this.descriptionSecondFull = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
