package com.example.mybookshopapp.data.model.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author karl
 */

public class ReadingModes {
    @JsonProperty("text")
    public boolean getText() {
        return this.text;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    boolean text;

    @JsonProperty("image")
    public boolean getImage() {
        return this.image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    boolean image;
}
