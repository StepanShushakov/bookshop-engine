package com.example.mybookshopapp.errs;

/**
 * @author karl
 */

public class UserExistsException extends Exception {
    public UserExistsException(String message) {
        super(message);
    }
}
