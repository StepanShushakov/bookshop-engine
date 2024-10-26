package com.example.mybookshopapp.data.dto;

import com.example.mybookshopapp.data.model.book.review.BookReviewEntity;

import java.time.format.DateTimeFormatter;

/**
 * @author karl
 */

public class BookReviewDto {

    private Integer id;
    private String userName;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int rating;
    private String time;

    public BookReviewDto(BookReviewEntity bookReviewEntity) {
        this.id = bookReviewEntity.getId();
        this.userName = bookReviewEntity.getUser().getName();
        this.text = bookReviewEntity.getText();
        this.time = bookReviewEntity.getTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public Integer getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
        this.rating = this.likeCount - this.dislikeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
        this.rating = this.likeCount - this.dislikeCount;
    }

    public int getRating() {
        return rating;
    }
}
