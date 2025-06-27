package com.web.appleshop.dto.response.admin;

import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;

import java.time.LocalDateTime;

public record OrderSummaryDto(
        Integer id,
        OrderStatus status,
        PaymentType paymentType,
        String createdName,
        LocalDateTime createdAt,
        String approveName,
        LocalDateTime approveAt
) {
}
