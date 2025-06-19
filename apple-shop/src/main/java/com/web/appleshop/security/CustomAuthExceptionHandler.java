package com.web.appleshop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.appleshop.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.ResourceClosedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class CustomAuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthExceptionHandler.class);

    /**
     * Phương thức này sẽ được gọi khi có lỗi xác thực (AuthenticationException).
     * Ví dụ: sai username/password, token không hợp lệ, chưa đăng nhập...
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        String errorCode = "UNAUTHORIZED";
        String errorMessage = authException.getMessage();

        if (authException instanceof DisabledException) {
            errorCode = "USER_DISABLED";
            errorMessage = "Tài khoản của bạn đã bị vô hiệu hóa hoặc chưa được kích hoạt.";
        } else if (authException instanceof BadCredentialsException) {
            errorMessage = "Email hoặc mật khẩu không chính xác.";
        } else if (authException instanceof InsufficientAuthenticationException) {
            errorMessage = "Bạn không có quyền truy cập vào tài nguyên này.";
        }

        log.warn("Throw exception: {}", authException.getClass());

        ApiResponse<Object> apiResponse = ApiResponse.error(
                errorCode,
                errorMessage
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, apiResponse);
        outputStream.flush();
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Tạo response lỗi tùy chỉnh
        ApiResponse<Object> apiResponse = ApiResponse.error(
                "ACCESS_DENIED",
                "Bạn không có quyền truy cập vào tài nguyên này."
        );

        // Thiết lập trạng thái HTTP là 403 Forbidden
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Ghi response JSON vào body của HTTP response
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, apiResponse);
        outputStream.flush();
    }
}
