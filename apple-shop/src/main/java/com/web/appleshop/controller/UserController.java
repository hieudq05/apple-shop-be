package com.web.appleshop.controller;

import com.web.appleshop.dto.projection.UserInfo;
import com.web.appleshop.dto.request.ChangePasswordDto;
import com.web.appleshop.dto.request.UserUpdateDto;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Manages user-specific operations.
 * <p>
 * This controller provides endpoints for authenticated users to retrieve their
 * profile information, update their details (including their profile picture),
 * and change their password.
 */
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
class UserController {
    private final UserService userService;

    /**
     * Retrieves the profile information of the currently authenticated user.
     *
     * @return A {@link ResponseEntity} containing the user's {@link UserInfo}.
     */
    @GetMapping("me")
    public ResponseEntity<ApiResponse<UserInfo>> getUserInfo() {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserInfo(), "Get user info successfully"));
    }

    /**
     * Updates the profile information of the currently authenticated user.
     * This can include both textual data and a new profile image.
     *
     * @param user The DTO containing the user data to update.
     * @param imageFile The new profile picture as a multipart file (optional).
     * @return A {@link ResponseEntity} containing the updated {@link UserUpdateDto}.
     */
    @PatchMapping(path = "me", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<UserUpdateDto>> updateUserInfo(
            @RequestPart @Valid UserUpdateDto user,
            @RequestPart(required = false) MultipartFile imageFile
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(user, imageFile), "Update user info successfully"));
    }

    /**
     * Changes the password for the currently authenticated user.
     *
     * @param changePasswordDto The DTO containing the old and new passwords.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping("change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(changePasswordDto);
        return ResponseEntity.ok(ApiResponse.success(null, "Change password successfully"));
    }
}
