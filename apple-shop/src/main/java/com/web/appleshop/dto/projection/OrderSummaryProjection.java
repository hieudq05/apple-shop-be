package com.web.appleshop.dto.projection;

import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;

import java.time.LocalDateTime;

/**
 * Projection for {@link com.web.appleshop.entity.Order}
 */
public interface OrderSummaryProjection {
    Integer getId();

    LocalDateTime getCreatedAt();

    PaymentType getPaymentType();

    LocalDateTime getApproveAt();

    OrderStatus getStatus();

    UserSummary getCreatedBy();

    interface UserSummary {
        Integer getId();
        String getFirstName();
        String getLastName();
        String getImage();
    }
}