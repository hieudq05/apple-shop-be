package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.admin.OrderAdminResponse;
import com.web.appleshop.entity.Order;
import com.web.appleshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final OrderService orderService;

    @GetMapping("{orderId}")
    public ResponseEntity<ApiResponse<OrderAdminResponse>> getOrder(@PathVariable Integer orderId) {
        OrderAdminResponse order = orderService.getOrderDetailByIdForAdmin(orderId);
        return ResponseEntity.ok(ApiResponse.success(order, "Get order successfully"));
    }
}
