package com.web.appleshop.controller;

import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.OrderUserResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("me")
    public ResponseEntity<ApiResponse<List<OrderUserResponse>>> getOrdersForUser(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<OrderUserResponse> orders = orderService.getOrdersForUser(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalPages(),
                orders.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(orders.getContent(), "Get orders successfully", pageableResponse));
    }

    @PostMapping("{orderId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable Integer orderId) {
        orderService.cancelOrderForUser(orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "Cancel order successfully"));
    }
}
