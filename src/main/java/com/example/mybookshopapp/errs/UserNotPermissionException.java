package com.example.mybookshopapp.errs;

/**
 * @author karl
 */

public class UserNotPermissionException extends Exception{
    public UserNotPermissionException(String message) {
        super(message);
    }
}
