package com.web.appleshop.exception;

import com.web.appleshop.dto.response.ValidationErrorDetail;

import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<ValidationErrorDetail> errors;

    public ValidationException(String message, List<ValidationErrorDetail> errors) {
        super(message);
        this.errors = errors;
    }

    public List<ValidationErrorDetail> getErrors() {
        return errors;
    }
}