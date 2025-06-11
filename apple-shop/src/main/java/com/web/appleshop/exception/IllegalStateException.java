package com.web.appleshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class IllegalStateException extends RuntimeException {
    public IllegalStateException(String message) {
        super(message);
    }
}
