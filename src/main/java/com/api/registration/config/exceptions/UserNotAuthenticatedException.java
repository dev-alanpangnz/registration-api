package com.api.registration.config.exceptions;

public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException(String username) {
        super("Please log into your account at " + username + ".");
    }
}
