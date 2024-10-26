package com.example.mybookshopapp.data.model.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author karl
 */

public class Offer {
    @JsonProperty("finskyOfferType")
    public int getFinskyOfferType() {
        return this.finskyOfferType;
    }

    public void setFinskyOfferType(int finskyOfferType) {
        this.finskyOfferType = finskyOfferType;
    }

    int finskyOfferType;

    @JsonProperty("listPrice")
    public ListPrice getListPrice() {
        return this.listPrice;
    }

    public void setListPrice(ListPrice listPrice) {
        this.listPrice = listPrice;
    }

    ListPrice listPrice;

    @JsonProperty("retailPrice")
    public RetailPrice getRetailPrice() {
        return this.retailPrice;
    }

    public void setRetailPrice(RetailPrice retailPrice) {
        this.retailPrice = retailPrice;
    }

    RetailPrice retailPrice;

    @JsonProperty("giftable")
    public boolean getGiftable() {
        return this.giftable;
    }

    public void setGiftable(boolean giftable) {
        this.giftable = giftable;
    }

    boolean giftable;

    @JsonProperty("rentalDuration")
    public RentalDuration getRentalDuration() {
        return this.rentalDuration;
    }

    public void setRentalDuration(RentalDuration rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    RentalDuration rentalDuration;
}
