package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.projection.OrderSummaryProjection;
import com.web.appleshop.dto.request.AdminCreateOrderRequest;
import com.web.appleshop.dto.request.AdminOrderSearchCriteria;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.OrderAdminResponse;
import com.web.appleshop.dto.response.admin.OrderSummaryV2Dto;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private static final Logger log = LoggerFactory.getLogger(AdminOrderController.class);
    private final OrderService orderService;

    @GetMapping("{orderId}")
    public ResponseEntity<ApiResponse<OrderAdminResponse>> getOrder(@PathVariable Integer orderId) {
        OrderAdminResponse order = orderService.getOrderDetailByIdForAdmin(orderId);
        return ResponseEntity.ok(ApiResponse.success(order, "Get order successfully"));
    }

    @GetMapping("search")
    public ResponseEntity<ApiResponse<List<OrderSummaryV2Dto>>> searchOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestBody AdminOrderSearchCriteria criteria
    ) {
        Pageable pageable = Pageable
                .ofSize(size != null ? size : 6)
                .withPage(page != null ? page : 0);
        Page<OrderSummaryV2Dto> orders = orderService.searchOrdersSummaryForAdmin(criteria, pageable);
        PageableResponse pageableResponse = new PageableResponse(
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalPages(),
                orders.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(orders.getContent(), "Search orders successfully", pageableResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderSummaryProjection>>> getOrderSummary(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(
                page  != null ? page : 0,
                size != null ? size : 6,
                sort
        );
        Page<OrderSummaryProjection> orderSummaryPage = orderService.getOrdersSummaryForAdmin(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                orderSummaryPage.getNumber(),
                orderSummaryPage.getSize(),
                orderSummaryPage.getTotalPages(),
                orderSummaryPage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(orderSummaryPage.getContent(), "Get order summary successfully", pageableResponse));
    }

    @PatchMapping("{orderId}/status")
    public ResponseEntity<ApiResponse<String>> updateOrderStatus(@PathVariable Integer orderId, @RequestParam String status) {
        OrderStatus newStatus = OrderStatus.valueOf(status);
        orderService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok(ApiResponse.success(null, "Update order status successfully"));
    }

    @PostMapping("{orderId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable Integer orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "Cancel order successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createOrder(@Valid @RequestBody AdminCreateOrderRequest[] orderRequests) {
        orderService.createOrder(orderRequests);
        return ResponseEntity.ok(ApiResponse.success(null, "Create order successfully"));
    }

    @PostMapping("v1")
    public ResponseEntity<ApiResponse<String>> createOrderV1(@Valid @RequestBody AdminCreateOrderRequest orderRequests) {
        orderService.createOrderWithPromotionForAdmin(orderRequests);
        return ResponseEntity.ok(ApiResponse.success(null, "Create order successfully"));
    }

    /**
     * Statistics
     */

    @GetMapping("statistics/total-revenue")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalRevenue(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        BigDecimal totalRevenue = orderService.getOrderTotalRevenue(
                status, fromDate, toDate
        );
        return ResponseEntity.ok(ApiResponse.success(
                totalRevenue == null ? BigDecimal.ZERO : totalRevenue
                , "Get total revenue successfully"));
    }

    @GetMapping("statistics/all-total-revenue")
    public ResponseEntity<ApiResponse<BigDecimal>> getAllTotalRevenue(
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        BigDecimal totalRevenue = orderService.getAllOrderTotalRevenue(
                fromDate, toDate
        );
        return ResponseEntity.ok(ApiResponse.success(
                totalRevenue == null ? BigDecimal.ZERO : totalRevenue
                , "Get total revenue successfully"));
    }

    @GetMapping("statistics/number-orders")
    public ResponseEntity<ApiResponse<Long>> getNumberOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        Long numberOrders = orderService.getNumberOfOrders(
                status, fromDate, toDate
        );
        return ResponseEntity.ok(ApiResponse.success(numberOrders, "Get number of orders successfully"));
    }

    @GetMapping("statistics/all-number-orders")
    public ResponseEntity<ApiResponse<Long>> getAllNumberOrders(
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        Long numberOrders = orderService.getAllNumberOfOrders(
                fromDate, toDate
        );
        return ResponseEntity.ok(ApiResponse.success(numberOrders, "Get number of orders successfully"));
    }

    @GetMapping("statistics/number-products-sold")
    public ResponseEntity<ApiResponse<Long>> getNumberProductsSold(
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        Long numberProductsSold = orderService.getNumberOfProductsSold(
                fromDate, toDate
        );
        return ResponseEntity.ok(ApiResponse.success(
                numberProductsSold == null ? 0L : numberProductsSold,
                "Get number of products sold successfully"));
    }
}
