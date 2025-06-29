package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.PaymentDto;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
class AdminPaymentController {
    private final OrderService orderService;

    @PostMapping("vnpay/payment-url")
    public ResponseEntity<ApiResponse<PaymentDto.VnPayResponse>> getPaymentUrl(
            HttpServletRequest request,
            @RequestParam Integer orderId
    ) {
        PaymentDto.VnPayResponse response = orderService.createVNPAYPaymentUrl(orderId, request);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Create payment successfully")
        );
    }
}
