package com.example.mybookshopapp.data.model.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author karl
 */

public class SearchInfo {
    @JsonProperty("textSnippet")
    public String getTextSnippet() {
        return this.textSnippet;
    }

    public void setTextSnippet(String textSnippet) {
        this.textSnippet = textSnippet;
    }

    String textSnippet;
}
