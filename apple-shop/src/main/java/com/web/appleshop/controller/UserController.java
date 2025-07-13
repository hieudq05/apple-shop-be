package com.web.appleshop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.appleshop.dto.projection.UserInfo;
import com.web.appleshop.dto.request.ChangePasswordDto;
import com.web.appleshop.dto.request.UserUpdateDto;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
class UserController {
    private final UserService userService;

    @GetMapping("me")
    public ResponseEntity<ApiResponse<UserInfo>> getUserInfo() {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserInfo(), "Get user info successfully"));
    }

    @PatchMapping(path = "me", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<String>> updateUserInfo(
            @RequestPart @Valid UserUpdateDto user,
            @RequestPart(required = false) MultipartFile imageFile
    ) {
        userService.updateUser(user, imageFile);
        return ResponseEntity.ok(ApiResponse.success(null, "Update user info successfully"));
    }

    @PostMapping("change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(changePasswordDto);
        return ResponseEntity.ok(ApiResponse.success(null, "Change password successfully"));
    }
}
