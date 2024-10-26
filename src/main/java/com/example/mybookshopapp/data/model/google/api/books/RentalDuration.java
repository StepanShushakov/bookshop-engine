package com.example.mybookshopapp.data.model.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author karl
 */

public class RentalDuration {
    @JsonProperty("unit")
    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    String unit;

    @JsonProperty("count")
    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    int count;
}
