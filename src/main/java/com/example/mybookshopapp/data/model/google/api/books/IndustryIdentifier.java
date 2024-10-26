package com.example.mybookshopapp.data.model.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author karl
 */

public class IndustryIdentifier {
    @JsonProperty("type")
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type;

    @JsonProperty("identifier")
    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    String identifier;
}
