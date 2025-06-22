package com.web.appleshop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Object meta;
    private ErrorResponse error;

    private ApiResponse() {
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.data = data;
        response.message = message;
        response.success = true;
        return response;
    }

    public static <T> ApiResponse<T> success(T data, String message, Object meta) {
        ApiResponse<T> response = new ApiResponse<>();
        response.data = data;
        response.message = message;
        response.success = true;
        response.meta = meta;
        return response;
    }

    public static <T> ApiResponse<T> error(ErrorResponse error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = error;
        return response;
    }

    public static <T> ApiResponse<T> error(String errorCode, String errorMessage) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = new ErrorResponse(errorCode, errorMessage);
        return response;
    }

    // ✅ Bổ sung cho controller dễ gọi
    public static <T> ApiResponse<T> ok(String message, T data) {
        return success(data, message);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return error("BAD_REQUEST", message);
    }
}

