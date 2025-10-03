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

/**
 * Handles administrative payment-related operations.
 * <p>
 * This controller provides endpoints for administrators to generate payment URLs
 * for existing orders, facilitating manual payment retries or other administrative
 * payment actions for both VnPay and PayPal.
 */
@RestController
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
class AdminPaymentController {
    private final OrderService orderService;

    /**
     * Generates a VnPay payment URL for a specific order.
     *
     * @param request The incoming HTTP request.
     * @param orderId The ID of the order for which to create the payment URL.
     * @return A {@link ResponseEntity} containing the {@link PaymentDto.VnPayResponse} with the payment URL.
     */
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

    /**
     * Generates a PayPal payment URL for a specific order.
     *
     * @param request The incoming HTTP request.
     * @param orderId The ID of the order for which to create the payment URL.
     * @return A {@link ResponseEntity} containing the {@link PaymentDto.PayPalResponse} with the payment approval link.
     */
    @PostMapping("paypal/payment-url")
    public ResponseEntity<ApiResponse<PaymentDto.PayPalResponse>> getPaypalPaymentUrl(
            HttpServletRequest request,
            @RequestParam Integer orderId
    ) {
        PaymentDto.PayPalResponse response = orderService.createPaypalPaymentUrl(orderId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Create payment successfully"));
    }
}
