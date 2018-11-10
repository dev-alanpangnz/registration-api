package com.api.registration.config.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotVerifiedException extends RuntimeException {
    public UserNotVerifiedException(String email) {
        super("Please verify your email at: '" + email + "'.");
    }
}
