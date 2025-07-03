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

import java.util.List;

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
}
