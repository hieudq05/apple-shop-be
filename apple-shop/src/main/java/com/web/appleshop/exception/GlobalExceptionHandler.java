package com.web.appleshop.exception;

import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.ValidationErrorDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(
            BadRequestException e
    ) {
        log.warn("Bad request from client: {}", e.getMessage());

        ApiResponse<Object> response = ApiResponse.error(String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(
            NotFoundException e
    ) {
        log.warn("Not found: {}", e.getMessage());

        ApiResponse<Object> response = ApiResponse.error(String.valueOf(HttpStatus.NOT_FOUND.value()), e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbiddenException(
            ForbiddenException e
    ) {
        log.warn("Forbidden: {}", e.getMessage());

        ApiResponse<Object> response = ApiResponse.error(String.valueOf(HttpStatus.FORBIDDEN.value()), e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(
            IllegalStateException e
    ) {
        log.warn("Illegal state: {}", e.getMessage());

        ApiResponse<Object> response = ApiResponse.error(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        log.warn("Validation error: {}", e.getMessage());

        // 1. Tạo danh sách để chứa các chi tiết lỗi
        List<ValidationErrorDetail> validationErrorDetails = new ArrayList<>();

        // 2. Lặp qua các lỗi từ exception và chuyển đổi thành ValidationErrorDetail
        for(FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            validationErrorDetails.add(new ValidationErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        // 3. Sử dụng ApiResponse để trả về lỗi
        ApiResponse<Object> response = ApiResponse.error(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                "Dữ liệu không hợp lệ.",
                validationErrorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
