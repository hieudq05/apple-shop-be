package com.web.appleshop.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

public class PaymentDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class VnPayRequest {
        long amount;
        String orderInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class VnPayResponse {
        String code;
        String message;
        String paymentUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class VnPayIpnResponse {
        String RspCode;
        String Message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PayPalResponse {
        private String code;
        private String message;
        private String paymentUrl;
        private String paymentId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PayPalExecuteResponse {
        private String code;
        private String message;
        private String paymentId;
        private String state;
        private Integer orderId;
    }
}
