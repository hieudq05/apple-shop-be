package com.web.appleshop.dto.response.admin;

import com.web.appleshop.dto.projection.OrderSummaryProjection;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderSummaryV2Dto {
    Integer id;

    LocalDateTime createdAt;

    PaymentType paymentType;

    LocalDateTime approveAt;

    OrderStatus status;

    UserSummary createdBy;

    @Data
    @AllArgsConstructor
    public static class UserSummary {
        Integer id;
        String firstName;
        String lastName;
        String image;
    }
}
