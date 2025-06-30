package com.web.appleshop.controller;

import com.web.appleshop.dto.PaymentDto;
import com.web.appleshop.dto.request.UserCreateOrderRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.entity.Order;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import com.web.appleshop.service.OrderService;
import com.web.appleshop.service.OrderStatusService;
import com.web.appleshop.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("payments")
@RequiredArgsConstructor
public class PaymentController {
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final OrderService orderService;
    private final VnPayService vnPayService;
    private final OrderStatusService orderStatusService;

    @PostMapping("vnpay/create-payment")
    public ResponseEntity<ApiResponse<PaymentDto.VnPayResponse>> createPayment(
            HttpServletRequest request,
            @RequestBody UserCreateOrderRequest orderRequest
    ) {
        Order order = orderService.createOrder(orderRequest, PaymentType.VNPAY);

        String paymentUrl = vnPayService.createPaymentUrl(
                request,
                orderService.calculateTotalPrice(order.getOrderDetails()).longValue(),
                "Thanh toan don hang #" + order.getId()
        );

        PaymentDto.VnPayResponse response = new PaymentDto.VnPayResponse(
                "00",
                "Tạo đường dẫn thanh toán thành công",
                paymentUrl
        );

        return ResponseEntity.ok(ApiResponse.success(response, "Create payment successfully"));
    }

    @PostMapping("vnpay/payment-url")
    public ResponseEntity<ApiResponse<PaymentDto.VnPayResponse>> vnpayReturnUrl(
            HttpServletRequest request,
            @RequestParam Integer orderId
    ) {
        PaymentDto.VnPayResponse response = orderService.createVNPAYPaymentUrlForUser(orderId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Create payment successfully"));
    }

    @GetMapping("vnpay-callback")
    public ResponseEntity<ApiResponse<PaymentDto.VnPayIpnResponse>> vnpayCallback(
            HttpServletRequest request
    ) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String transactionId = request.getParameter("vnp_TxnRef");
        String responseCode = request.getParameter("vnp_ResponseCode");

        if (paymentStatus == 1) {
            orderStatusService.updateStatus(
                    Integer.parseInt(orderInfo.split("#")[1]),
                    OrderStatus.PAID
            );
            log.info("Payment successful. Transaction ID: {}, Response code: {}", transactionId, responseCode);
            return ResponseEntity.ok(ApiResponse.success(
                    new PaymentDto.VnPayIpnResponse("00", "Thanh toán thành công"),
                    "Payment successful"
            ));
        } else if (paymentStatus == 0) {
            orderStatusService.updateStatus(
                    Integer.parseInt(orderInfo.split("#")[1]),
                    OrderStatus.FAILED_PAYMENT
            );
            log.info("Payment failed. Transaction ID: {}, Response code: {}", transactionId, responseCode);
            return ResponseEntity.ok(ApiResponse.success(
                    new PaymentDto.VnPayIpnResponse("99", "Thanh toán thất bại"),
                    "Payment failed"
            ));
        } else {
            log.info("Payment failed. Transaction ID: {}, Response code: {}", transactionId, responseCode);
            return ResponseEntity.ok(ApiResponse.success(
                    new PaymentDto.VnPayIpnResponse("99", "Chữ ký không hợp lệ."),
                    "Payment failed"
            ));
        }
    }
}
