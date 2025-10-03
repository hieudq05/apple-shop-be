package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.projection.UserAdminSummaryInfo;
import com.web.appleshop.dto.request.UserSearchCriteria;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.UserAdminInfoDto;
import com.web.appleshop.dto.response.admin.UserAdminSummaryDto;
import com.web.appleshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles administrative operations related to users.
 * <p>
 * This controller provides endpoints for managing users from the admin panel,
 * including viewing, searching, enabling/disabling accounts, and updating roles.
 * It also offers various user-related statistical endpoints.
 */
@RestController
@RequestMapping("admin/users")
@RequiredArgsConstructor
class AdminUserController {
    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);
    private final UserService userService;

    /**
     * Retrieves a paginated list of all users for the admin panel.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of users per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of {@link UserAdminSummaryInfo}.
     */
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

    /**
     * Retrieves detailed information for a specific user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return A {@link ResponseEntity} containing the {@link UserAdminInfoDto}.
     */
    @GetMapping("{userId}")
    public ResponseEntity<ApiResponse<UserAdminInfoDto>> getUserById(@PathVariable Integer userId) {
        UserAdminInfoDto user = userService.getUserInfoForAdmin(userId);
        return ResponseEntity.ok(ApiResponse.success(user, "Get user successfully"));
    }

    /**
     * Searches for users based on specified criteria.
     *
     * @param page     The page number for pagination (optional, defaults to 0).
     * @param size     The page size for pagination (optional, defaults to 6).
     * @param criteria The criteria to search users by.
     * @return A {@link ResponseEntity} with a paginated list of found users.
     */
    @PostMapping("search")
    public ResponseEntity<ApiResponse<List<UserAdminSummaryDto>>> searchUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestBody UserSearchCriteria criteria
    ) {
        log.info("Search users with criteria: {}", criteria);
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

    /**
     * Toggles the enabled/disabled status of a user's account.
     *
     * @param userId The ID of the user to update.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping("{userId}/toggle-enabled")
    public ResponseEntity<ApiResponse<Void>> toggleUserEnabled(@PathVariable Integer userId) {
        userService.toggleUserEnabled(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "User enabled/disabled successfully"));
    }

    /**
     * Updates the roles assigned to a specific user.
     *
     * @param userId The ID of the user to update.
     * @param role   A set of role names to assign to the user.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping("{userId}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(@PathVariable Integer userId, @RequestBody Set<String> role) {
        userService.setRoleforUser(userId, role);
        return ResponseEntity.ok(ApiResponse.success(null, "User role updated successfully"));
    }

    /**
     * Retrieves the count of new users within a specified date range.
     *
     * @param fromDate The start of the date range (optional).
     * @param toDate   The end of the date range (optional).
     * @return A {@link ResponseEntity} containing the count of new users.
     */
    @GetMapping("statistics/new-user-count")
    public ResponseEntity<ApiResponse<Long>> getNewUserCount(
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getNumberOfNewUsers(fromDate, toDate), "Get new user count successfully"));
    }

    /**
     * Retrieves a list of top users based on their total spending.
     *
     * @param limit    The maximum number of top users to return (optional).
     * @param fromDate The start date for the statistics (optional).
     * @param toDate   The end date for the statistics (optional).
     * @return A {@link ResponseEntity} containing a list of top users by expense.
     */
    @GetMapping("statistics/top-users-expense")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getActiveUserCount(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getTopUserByPrice(limit, fromDate, toDate).getContent(), "Get active user count successfully"));
    }

    /**
     * Retrieves a list of top users based on their total number of orders.
     *
     * @param limit    The maximum number of top users to return (optional).
     * @param fromDate The start date for the statistics (optional).
     * @param toDate   The end date for the statistics (optional).
     * @return A {@link ResponseEntity} containing a list of top users by order count.
     */
    @GetMapping("statistics/top-users-order-count")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopUserOrderCount(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getTopUserByOrderCount(limit, fromDate, toDate).getContent(), "Get top user order count successfully"));
    }
}
