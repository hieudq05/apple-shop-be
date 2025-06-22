package com.web.appleshop.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.appleshop.dto.CancelOrderDTO;
import com.web.appleshop.service.OrderService;

// AdminOrderController.java
@RestController
@RequestMapping("orders")
public class AdminOrderController {
    private final OrderService orderService; // Sử dụng interface

    @Autowired
    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelOrder(
            @PathVariable Integer id,
            @RequestBody(required = false) CancelOrderDTO cancelDTO) {
        
        String reason = (cancelDTO != null) ? cancelDTO.getReason() : null;
        orderService.cancelOrder(id, reason);
        
        return ResponseEntity.ok().body(
            Collections.singletonMap("message", "Đơn hàng đã được hủy thành công")
        );
    }
}
