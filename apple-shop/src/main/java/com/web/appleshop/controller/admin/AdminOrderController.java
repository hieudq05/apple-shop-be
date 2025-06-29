package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.projection.OrderSummaryProjection;
import com.web.appleshop.dto.request.AdminCreateOrderRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.OrderAdminResponse;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import com.web.appleshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderSummaryProjection>>> getOrderSummary(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = Pageable
                .ofSize(size != null ? size : 6)
                .withPage(page != null ? page : 0);
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
    public ResponseEntity<ApiResponse<String>> createOrder(@RequestBody AdminCreateOrderRequest[] orderRequests) {
        orderService.createOrder(orderRequests, PaymentType.VNPAY);
        return ResponseEntity.ok(ApiResponse.success(null, "Create order successfully"));
    }

}
