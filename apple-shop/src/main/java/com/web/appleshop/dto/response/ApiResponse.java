package com.web.appleshop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // Không trả về trường có giá trị null
@Getter
@Setter
public class ApiResponse<T> {

    private boolean success;
    private String msg;
    private T data;
    private Object meta;
    private ErrorResponse error;

    private ApiResponse() {
        //  Đặt constructor mặc định là private để tránh việc tạo instance trực tiếp từ lớp này
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.data = data;
        response.msg = message;
        response.success = true;
        return response;
    }

    public static <T> ApiResponse<T> success(T data, String message, Object meta) {
        ApiResponse<T> response = new ApiResponse<>();
        response.data = data;
        response.msg = message;
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

    public static <T> ApiResponse<T> error(String errorCode, String errorMessage, List<ValidationErrorDetail> errors) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = new ErrorResponse(errorCode, errorMessage, errors);
        return response;
    }
}
