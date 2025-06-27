package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.projection.OrderSummaryProjection;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.OrderAdminResponse;
import com.web.appleshop.dto.response.admin.OrderSummaryDto;
import com.web.appleshop.entity.Order;
import com.web.appleshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderSummaryProjection>>> getOrderSummary(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Boolean asc
            ) {
        Pageable pageable = Pageable
                .ofSize(size != null ? size : 6)
                .withPage(page != null ? page : 0)
                ;
        Page<OrderSummaryProjection> orderSummaryPage = orderService.getOrdersSummaryForAdmin(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                orderSummaryPage.getNumber(),
                orderSummaryPage.getSize(),
                orderSummaryPage.getTotalPages(),
                orderSummaryPage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(orderSummaryPage.getContent(), "Get order summary successfully", pageableResponse));
    }

}
