package com.example.mybookshopapp.errs;

/**
 * @author karl
 */

public class BadRequestException extends Exception {

    public BadRequestException(String message) {
        super(message);
    }
}
