package com.web.appleshop.controller;

import com.web.appleshop.dto.PaymentDto;
import com.web.appleshop.dto.request.UserCreateOrderRequest;
import com.web.appleshop.dto.request.UserCreateOrderWithPromotionRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.entity.Order;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import com.web.appleshop.service.OrderService;
import com.web.appleshop.service.OrderStatusService;
import com.web.appleshop.service.PayPalService;
import com.web.appleshop.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

/**
 * Handles payment processing through various gateways like VnPay and PayPal.
 * <p>
 * This controller provides endpoints to create payment URLs, handle callbacks,
 * and process payment results for orders. It integrates with different payment
 * services to manage the entire payment lifecycle.
 */
@RestController
@RequestMapping("payments")
@RequiredArgsConstructor
public class PaymentController {
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final OrderService orderService;
    private final VnPayService vnPayService;
    private final OrderStatusService orderStatusService;
    private final PayPalService payPalService;
    @Value("${public.base.url}")
    private String publicBaseUrl;

    /**
     * Creates a VnPay payment URL for a new order.
     *
     * @param request The incoming HTTP request.
     * @param orderRequest The request to create a new order.
     * @return A {@link ResponseEntity} with the VnPay payment URL.
     */
    @PostMapping("vnpay/create-payment")
    @Transactional
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

    /**
     * Creates a VnPay payment URL for a new order with a promotion applied.
     *
     * @param request The incoming HTTP request.
     * @param orderRequest The request to create a new order with a promotion.
     * @return A {@link ResponseEntity} with the VnPay payment URL.
     */
    @PostMapping("vnpay/create-payment-v1")
    public ResponseEntity<?> createVNPAYOrderWithPromotion(
            HttpServletRequest request,
            @Valid @RequestBody UserCreateOrderWithPromotionRequest orderRequest) {
        Order order = orderService.createOrderWithPromotion(orderRequest, PaymentType.VNPAY);

        String paymentUrl = vnPayService.createPaymentUrl(
                request,
                order.getFinalTotal().longValue(),
                "Thanh toan don hang #" + order.getId()
        );

        PaymentDto.VnPayResponse response = new PaymentDto.VnPayResponse(
                "00",
                "Tạo đường dẫn thanh toán thành công",
                paymentUrl
        );

        return ResponseEntity.ok(ApiResponse.success(response, "Create payment successfully"));
    }

    /**
     * Creates a VnPay payment URL for an existing order.
     *
     * @param request The incoming HTTP request.
     * @param orderId The ID of the existing order.
     * @return A {@link ResponseEntity} with the VnPay payment URL.
     */
    @PostMapping("vnpay/create-payment-v1/order/{orderId}")
    public ResponseEntity<?> createVNPAYOrderWithPromotion(HttpServletRequest request, @PathVariable Integer orderId) {
        Order order = orderService.getOrderById(orderId);

        String paymentUrl = vnPayService.createPaymentUrl(
                request,
                order.getFinalTotal().longValue(),
                "Thanh toan don hang #" + order.getId()
        );

        PaymentDto.VnPayResponse response = new PaymentDto.VnPayResponse(
                "00",
                "Tạo đường dẫn thanh toán thành công",
                paymentUrl
        );
        return ResponseEntity.ok(ApiResponse.success(response, "Create payment successfully"));
    }

    /**
     * Generates a VnPay payment URL for an existing order, intended for retrying payment.
     *
     * @param request The incoming HTTP request.
     * @param orderId The ID of the order to create a payment URL for.
     * @return A {@link ResponseEntity} with the VnPay payment URL.
     */
    @PostMapping("vnpay/payment-url")
    @Transactional
    public ResponseEntity<ApiResponse<PaymentDto.VnPayResponse>> vnpayReturnUrl(
            HttpServletRequest request,
            @RequestParam Integer orderId
    ) {
        PaymentDto.VnPayResponse response = orderService.createVNPAYPaymentUrlForUser(orderId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Create payment successfully"));
    }

    /**
     * Handles the callback from VnPay after a payment attempt.
     * Updates the order status based on the payment result and redirects the user to a results page.
     *
     * @param request The incoming HTTP request containing VnPay callback parameters.
     * @return A {@link RedirectView} to the frontend payment result page.
     */
    @GetMapping("vnpay/call-back")
    @Transactional
    public RedirectView vnpayCallback(
            HttpServletRequest request
    ) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String transactionId = request.getParameter("vnp_TxnRef");
        String responseCode = request.getParameter("vnp_ResponseCode");
        String transactionStatus = request.getParameter("vnp_TransactionStatus");
        String amount = request.getParameter("vnp_Amount");
        String bankCode = request.getParameter("vnp_BankCode");
        String bankTranNo = request.getParameter("vnp_BankTranNo");
        String cardType = request.getParameter("vnp_CardType");
        String payDate = request.getParameter("vnp_PayDate");
        String transactionNo = request.getParameter("vnp_TransactionNo");

        if (paymentStatus == 1) {
            orderStatusService.updateStatus(
                    Integer.parseInt(orderInfo.split("#")[1]),
                    OrderStatus.PAID
            );
            String returnUrl = UriComponentsBuilder.fromHttpUrl("https://hieudq05.github.io/apple-shop-fe.github.io/payment-result")
                    .queryParam("transactionId", transactionId)
                    .queryParam("ResponseCode", responseCode)
                    .queryParam("TransactionStatus", transactionStatus)
                    .queryParam("Amount", amount)
                    .queryParam("BankCode", bankCode)
                    .queryParam("BankTranNo", bankTranNo)
                    .queryParam("CardType", cardType)
                    .queryParam("PayDate", payDate)
                    .queryParam("TransactionNo", transactionNo)
                    .queryParam("OrderInfo", orderInfo)
                    .queryParam("TxnRef", transactionId)
                    .toUriString();
            log.info("Payment successful. Transaction ID: {}, Response code: {}", transactionId, responseCode);
            return new RedirectView(returnUrl);
        } else if (paymentStatus == 0) {
            orderStatusService.updateStatus(
                    Integer.parseInt(orderInfo.split("#")[1]),
                    OrderStatus.FAILED_PAYMENT
            );
            String returnUrl = UriComponentsBuilder.fromHttpUrl("https://hieudq05.github.io/apple-shop-fe.github.io/payment-result")
                    .queryParam("transactionId", transactionId)
                    .queryParam("ResponseCode", responseCode)
                    .queryParam("TransactionStatus", transactionStatus)
                    .queryParam("Amount", amount)
                    .queryParam("BankCode", bankCode)
                    .queryParam("BankTranNo", bankTranNo)
                    .queryParam("CardType", cardType)
                    .queryParam("PayDate", payDate)
                    .queryParam("TransactionNo", transactionNo)
                    .queryParam("OrderInfo", orderInfo)
                    .queryParam("TxnRef", transactionId)
                    .toUriString();
            log.info("Payment failed. Transaction ID: {}, Response code: {}", transactionId, responseCode);
            return new RedirectView(returnUrl);
//            return ResponseEntity.ok(ApiResponse.success(
//                    new PaymentDto.VnPayIpnResponse("99", "Thanh toán thất bại"),
//                    "Payment failed"
//            ));
        } else {
            String returnUrl = UriComponentsBuilder.fromHttpUrl("https://hieudq05.github.io/apple-shop-fe.github.io/payment-result")
                    .queryParam("transactionId", transactionId)
                    .queryParam("ResponseCode", responseCode)
                    .queryParam("TransactionStatus", transactionStatus)
                    .queryParam("Amount", amount)
                    .queryParam("BankCode", bankCode)
                    .queryParam("BankTranNo", bankTranNo)
                    .queryParam("CardType", cardType)
                    .queryParam("PayDate", payDate)
                    .queryParam("TransactionNo", transactionNo)
                    .queryParam("OrderInfo", orderInfo)
                    .queryParam("TxnRef", transactionId)
                    .toUriString();
            log.info("Payment failed. Transaction ID: {}, Response code: {}", transactionId, responseCode);
            return new RedirectView(returnUrl);
//            return ResponseEntity.ok(ApiResponse.success(
//                    new PaymentDto.VnPayIpnResponse("99", "Chữ ký không hợp lệ."),
//                    "Payment failed"
//            ));
        }
    }

    /**
     * Creates a PayPal payment for a new order.
     *
     * @param orderRequest The request to create a new order.
     * @return A {@link ResponseEntity} with the PayPal payment details, including the approval link.
     */
    @PostMapping("paypal/create-payment")
    @Transactional
    public ResponseEntity<ApiResponse<PaymentDto.PayPalResponse>> createPayPalPayment(
            @RequestBody UserCreateOrderRequest orderRequest
    ) {
        Order order = orderService.createOrder(orderRequest, PaymentType.PAYPAL);

        BigDecimal totalAmount = orderService.calculateTotalPrice(order.getOrderDetails());

        PaymentDto.PayPalResponse response = payPalService.createPayment(
                totalAmount.doubleValue(),
                "USD",
                "paypal",
                "sale",
                "Payment for order #" + order.getId(),
                publicBaseUrl + "/payments/paypal/cancel",
                publicBaseUrl + "/payments/paypal/success",
                order.getId()
        );

        return ResponseEntity.ok(ApiResponse.success(response, "PayPal payment created successfully"));
    }

    /**
     * Creates a PayPal payment for a new order with a promotion applied.
     *
     * @param request The incoming HTTP request.
     * @param orderRequest The request to create a new order with a promotion.
     * @return A {@link ResponseEntity} with the PayPal payment details.
     */
    @PostMapping("paypal/create-payment-v1")
    public ResponseEntity<ApiResponse<PaymentDto.PayPalResponse>> createPAYPALOrderWithPromotion(
            HttpServletRequest request,
            @Valid @RequestBody UserCreateOrderWithPromotionRequest orderRequest) {
        Order order = orderService.createOrderWithPromotion(orderRequest, PaymentType.PAYPAL);

        PaymentDto.PayPalResponse payPalResponse = payPalService.createPayment(
                order.getFinalTotal().doubleValue(),
                "USD",
                "paypal",
                "sale",
                "Payment for order #" + order.getId(),
                publicBaseUrl + "/payments/paypal/cancel",
                publicBaseUrl + "/payments/paypal/success",
                order.getId()
        );

        return ResponseEntity.ok(ApiResponse.success(payPalResponse, "Create payment successfully"));
    }

    /**
     * Creates a PayPal payment for an existing order.
     *
     * @param request The incoming HTTP request.
     * @param orderId The ID of the existing order.
     * @return A {@link ResponseEntity} with the PayPal payment details.
     */
    @PostMapping("paypal/create-payment-v1/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentDto.PayPalResponse>> createPAYPALOrderWithPromotion(HttpServletRequest request, @PathVariable Integer orderId) {
        Order order = orderService.getOrderById(orderId);

        PaymentDto.PayPalResponse payPalResponse = payPalService.createPayment(
                order.getFinalTotal().doubleValue(),
                "USD",
                "paypal",
                "sale",
                "Payment for order #" + order.getId(),
                publicBaseUrl + "/payments/paypal/cancel",
                publicBaseUrl + "/payments/paypal/success",
                order.getId()
        );

        return ResponseEntity.ok(ApiResponse.success(
                payPalResponse,
                "Create payment successfully"
        ));
    }

    /**
     * Generates a PayPal payment URL for an existing order.
     *
     * @param orderId The ID of the order to create a payment URL for.
     * @return A {@link ResponseEntity} with the PayPal payment details.
     */
    @PostMapping("paypal/payment-url")
    public ResponseEntity<ApiResponse<PaymentDto.PayPalResponse>> createPayPalPaymentUrl(
            @RequestParam Integer orderId
    ) {
        Order order = orderService.getOrderById(orderId);
        BigDecimal totalAmount = orderService.calculateTotalPrice(order.getOrderDetails());

        PaymentDto.PayPalResponse response = payPalService.createPayment(
                totalAmount.doubleValue(),
                "USD",
                "paypal",
                "sale",
                "Payment for order #" + order.getId(),
                publicBaseUrl + "/payments/paypal/cancel",
                publicBaseUrl + "/payments/paypal/success",
                order.getId()
        );

        return ResponseEntity.ok(ApiResponse.success(response, "PayPal payment URL created successfully"));
    }

    /**
     * Handles the successful completion of a PayPal payment.
     * Executes the payment, updates the order status, and redirects the user to a results page.
     *
     * @param paymentId The payment ID provided by PayPal.
     * @param payerId The payer ID provided by PayPal.
     * @return A {@link RedirectView} to the frontend payment result page.
     */
    @GetMapping("paypal/success")
    @Transactional
    public RedirectView paypalSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ) {
        PaymentDto.PayPalExecuteResponse response = payPalService.executePayment(paymentId, payerId);

        if ("00".equals(response.getCode()) && "approved".equals(response.getState())) {
            orderStatusService.updateStatus(response.getOrderId(), OrderStatus.PAID);
            log.info("PayPal payment successful. Payment ID: {}", paymentId);
        } else {
            orderStatusService.updateStatus(response.getOrderId(), OrderStatus.FAILED_PAYMENT);
            log.warn("PayPal payment failed. Payment ID: {}", paymentId);
        }

        String returnUrl = UriComponentsBuilder.fromHttpUrl("https://hieudq05.github.io/apple-shop-fe.github.io/payment-result")
                .queryParam("TransactionId", response.getPaymentId())
                .queryParam("ResponseCode", response.getCode())
                .queryParam("TransactionStatus", response.getState())
                .queryParam("OrderInfo", response.getMessage() + " #" + response.getOrderId())
                .queryParam("TxnRef", response.getPaymentId())
                .queryParam("TransactionNo", response.getPaymentId())
                .toUriString();


        return new RedirectView(returnUrl);
    }

    /**
     * Handles the cancellation of a PayPal payment.
     *
     * @param token The token associated with the cancelled payment.
     * @return A {@link ResponseEntity} confirming the cancellation.
     */
    @GetMapping("paypal/cancel")
    @Transactional
    public ResponseEntity<ApiResponse<String>> paypalCancel(
            @RequestParam("token") String token
    ) {
        log.info("PayPal payment cancelled. Token: {}", token);
        return ResponseEntity.ok(ApiResponse.success("Payment cancelled", "PayPal payment was cancelled"));
    }
}
