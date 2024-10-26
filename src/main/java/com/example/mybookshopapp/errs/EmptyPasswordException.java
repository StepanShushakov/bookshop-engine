package com.example.mybookshopapp.errs;

/**
 * @author karl
 */

public class EmptyPasswordException extends Exception{
    public EmptyPasswordException(String message) {
        super(message);
    }
}
