package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.projection.UserAdminSummaryInfo;
import com.web.appleshop.dto.request.UserSearchCriteria;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.UserAdminInfoDto;
import com.web.appleshop.dto.response.admin.UserAdminSummaryDto;
import com.web.appleshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("admin/users")
@RequiredArgsConstructor
class AdminUserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserAdminSummaryInfo>>> getAllUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<UserAdminSummaryInfo> users = userService.getListUserSummary(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                users.getNumber(),
                users.getSize(),
                users.getTotalPages(),
                users.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.success(users.getContent(), "Get all users successfully", pageableResponse)
        );
    }

    @GetMapping("{userId}")
    public ResponseEntity<ApiResponse<UserAdminInfoDto>> getUserById(@PathVariable Integer userId) {
        UserAdminInfoDto user = userService.getUserInfoForAdmin(userId);
        return ResponseEntity.ok(ApiResponse.success(user, "Get user successfully"));
    }

    @GetMapping("search")
    public ResponseEntity<ApiResponse<List<UserAdminSummaryDto>>> searchUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestBody UserSearchCriteria criteria
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<UserAdminSummaryDto> users = userService.searchUsers(criteria, pageable);
        PageableResponse pageableResponse = new PageableResponse(
                users.getNumber(),
                users.getSize(),
                users.getTotalPages(),
                users.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(users.getContent(), "Search users successfully", pageableResponse));
    }

    @PutMapping("{userId}/toggle-enabled")
    public ResponseEntity<ApiResponse<Void>> toggleUserEnabled(@PathVariable Integer userId) {
        userService.toggleUserEnabled(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "User enabled/disabled successfully"));
    }

    @PutMapping("{userId}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(@PathVariable Integer userId, @RequestBody Set<String> role) {
        userService.setRoleforUser(userId, role);
        return ResponseEntity.ok(ApiResponse.success(null, "User role updated successfully"));
    }

    /**
     * Statistics
     */

    @GetMapping("statistics/new-user-count")
    public ResponseEntity<ApiResponse<Long>> getNewUserCount(
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getNumberOfNewUsers(fromDate, toDate), "Get new user count successfully"));
    }

    @GetMapping("statistics/top-users-expense")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getActiveUserCount(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getTopUserByPrice(limit, fromDate, toDate).getContent(), "Get active user count successfully"));
    }

    @GetMapping("statistics/top-users-order-count")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopUserOrderCount(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getTopUserByOrderCount(limit, fromDate, toDate).getContent(), "Get top user order count successfully"));
    }
}
