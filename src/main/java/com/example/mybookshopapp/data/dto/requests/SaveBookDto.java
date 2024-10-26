package com.example.mybookshopapp.data.dto.requests;

import com.example.mybookshopapp.data.model.book.BookEntity;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;

/**
 * @author karl
 */

@Getter
@Setter
public class SaveBookDto{

    private int id;
    private String slug;
    private String image;
    private String title;
    private String description;
    private String pubDate;
    private short isBestseller;
    private int price;
    private short discount;
    private String name;    //field always be null
    private String authorsIds;

    public SaveBookDto() {

    }

    public SaveBookDto(BookEntity bookEntity) {
        if (bookEntity == null) {
            return;
        }
        this.id = bookEntity.getId();
        this.slug = bookEntity.getSlug();
        this.image = bookEntity.getImage();
        this.title = bookEntity.getTitle();
        this.description = bookEntity.getDescription();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        this.pubDate = formatter.format(bookEntity.getPubDate());
        this.isBestseller = bookEntity.getIsBestseller();
        this.price = bookEntity.getPrice();
        this.discount = bookEntity.getDiscount();
    }
}
