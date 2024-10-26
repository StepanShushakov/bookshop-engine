package com.example.mybookshopapp.data.dto;

import com.example.mybookshopapp.data.model.other.DocumentEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author karl
 */

@Setter
@Getter
public class DocumentDto {

    private String slug;
    private String title;
    private String text;
    private String shortText;

    public DocumentDto(DocumentEntity documentEntity) {
        this.slug = documentEntity.getSlug();
        this.title = documentEntity.getTitle();
        this.text = documentEntity.getText();
        this.shortText = this.text.substring(0, 300);
    }
}
