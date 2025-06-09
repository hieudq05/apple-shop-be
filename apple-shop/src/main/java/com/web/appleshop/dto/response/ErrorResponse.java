package com.web.appleshop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String code, String message, List<ValidationErrorDetail> errors) {
    public ErrorResponse(String code, String message) {
        this(code, message, null);
    }
}
