package com.example.mybookshopapp.errs;

/**
 * @author karl
 */

public class BookstoreApiWrongParameterException extends Exception {
    public BookstoreApiWrongParameterException(String message) {
        super(message);
    }
}
