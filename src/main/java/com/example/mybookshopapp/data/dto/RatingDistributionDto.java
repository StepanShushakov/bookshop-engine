package com.example.mybookshopapp.data.dto;

import com.example.mybookshopapp.data.model.book.BookEntity;

/**
 * @author karl
 */

public class RatingDistributionDto {
    private int one;
    private int two;
    private int three;
    private int four;
    private int five;

    public RatingDistributionDto(BookEntity bookEntity) {
        bookEntity.getBookRatingEntityList().stream().forEach(bookRatingEntity -> {
             switch (bookRatingEntity.getValue()) {
                 case 1:
                     ++this.one;
                     break;
                 case 2:
                     ++this.two;
                     break;
                 case 3:
                     ++this.three;
                     break;
                 case 4:
                     ++this.four;
                     break;
                 case 5:
                     ++this.five;
                     break;
             }
        });
    }

    public int getOne() {
        return one;
    }

    public void setOne(int one) {
        this.one = one;
    }

    public int getTwo() {
        return two;
    }

    public void setTwo(int two) {
        this.two = two;
    }

    public int getThree() {
        return three;
    }

    public void setThree(int three) {
        this.three = three;
    }

    public int getFour() {
        return four;
    }

    public void setFour(int four) {
        this.four = four;
    }

    public int getFive() {
        return five;
    }

    public void setFive(int five) {
        this.five = five;
    }
}
