package com.example.mybookshopapp.data.dto;

import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.model.book.BookRatingEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author karl
 */

@Getter
@Setter
public class BookDto {

    private Integer id;
    private String slug;
    private String image;
    private String authors;
    private String title;
    private int discount;
    private Boolean isBestseller;
    private Integer rating;
    private String status;
    private int price;
    private int discountPrice;

    public BookDto(BookEntity bookEntity) {
        this.id = bookEntity.getId();
        this.slug = bookEntity.getSlug();
        this.image = bookEntity.getImage();
//        this.authors = ;  //устанавливается при конвертации сущности (entity) в объект передачи данных (dto)
        this.title = bookEntity.getTitle();
        this.discount = bookEntity.getDiscount();
        this.isBestseller = bookEntity.getIsBestseller() == 1;

        List<BookRatingEntity> bookRatingEntityList = bookEntity.getBookRatingEntityList();
        if (bookRatingEntityList == null
                || bookRatingEntityList.isEmpty()) {
            this.rating = 0;
        } else {
            int sumRating = 0;
            for (BookRatingEntity br : bookRatingEntityList) {
                sumRating = sumRating + br.getValue();
            }
            this.rating = Math.toIntExact(Math.round((double) sumRating / bookRatingEntityList.size()));
        }
//        this.status = ;
        this.price = bookEntity.getPrice();
        this.discountPrice = this.price - Math.toIntExact(Math.round(this.price * (double) this.discount / 100));
    }

    public Boolean getBestseller() {
        return isBestseller;
    }

    public void setBestseller(Boolean bestseller) {
        isBestseller = bestseller;
    }
}
