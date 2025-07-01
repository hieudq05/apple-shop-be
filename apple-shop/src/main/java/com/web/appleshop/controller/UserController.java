package com.web.appleshop.controller;

import com.web.appleshop.dto.projection.UserInfo;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
class UserController {
    private final UserService userService;

    @GetMapping("me")
    public ResponseEntity<ApiResponse<UserInfo>> getUserInfo() {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserInfo(), "Get user info successfully"));
    }
}
