package com.api.registration.config.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class EmailSenderServiceBlockedException extends RuntimeException{
    public EmailSenderServiceBlockedException() {
        super("Try tethering: email service either blocked by firewall or disconnected.");
    }
}
