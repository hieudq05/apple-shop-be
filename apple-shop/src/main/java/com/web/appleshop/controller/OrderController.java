package com.web.appleshop.controller;

import com.web.appleshop.dto.request.UserOrderSearchCriteria;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.OrderUserResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.UserOrderDetailResponse;
import com.web.appleshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles HTTP requests related to user orders.
 * <p>
 * This controller provides endpoints for authenticated users to view their order history,
 * see the details of a specific order, search their orders, and cancel an order.
 * All operations are performed within the context of the currently logged-in user.
 */
@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * Retrieves a paginated list of orders for the currently authenticated user.
     * <p>
     * The results are sorted by creation date in descending order.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of orders per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of {@link OrderUserResponse}.
     */
    @GetMapping("me")
    public ResponseEntity<ApiResponse<List<OrderUserResponse>>> getOrdersForUser(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 6,
                sort
        );
        Page<OrderUserResponse> orders = orderService.getOrdersForUser(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalPages(),
                orders.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(orders.getContent(), "Get orders successfully", pageableResponse));
    }

    /**
     * Retrieves the details of a specific order for the currently authenticated user.
     *
     * @param orderId The ID of the order to retrieve.
     * @return A {@link ResponseEntity} containing the {@link UserOrderDetailResponse} if found.
     */
    @GetMapping("{orderId}")
    public ResponseEntity<ApiResponse<UserOrderDetailResponse>> getOrderDetailForUser(@PathVariable Integer orderId) {
        UserOrderDetailResponse order = orderService.getOrderDetailByIdForUser(orderId);
        return ResponseEntity.ok(ApiResponse.success(order, "Get order successfully"));
    }

    /**
     * Searches for orders belonging to the currently authenticated user based on specified criteria.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of orders per page (optional, defaults to 6).
     * @param criteria The search criteria.
     * @return A {@link ResponseEntity} containing a paginated list of matching {@link OrderUserResponse}.
     */
    @PostMapping("search")
    public ResponseEntity<ApiResponse<List<OrderUserResponse>>> searchOrdersForUser(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestBody UserOrderSearchCriteria criteria) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 6, sort);
        Page<OrderUserResponse> orders = orderService.searchOrdersForUser(criteria, pageable);
        PageableResponse pageableResponse = new PageableResponse(
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalPages(),
                orders.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(orders.getContent(), "Search orders successfully", pageableResponse));
    }

    /**
     * Cancels a specific order for the currently authenticated user.
     *
     * @param orderId The ID of the order to cancel.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping("{orderId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable Integer orderId) {
        orderService.cancelOrderForUser(orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "Cancel order successfully"));
    }
}
