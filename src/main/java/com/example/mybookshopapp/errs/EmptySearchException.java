package com.example.mybookshopapp.errs;

/**
 * @author karl
 */

public class EmptySearchException extends Exception {
    public EmptySearchException(String message) {
        super(message);
    }
}
