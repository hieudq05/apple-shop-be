package com.web.appleshop.dto.request;

import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminOrderSearchCriteria extends BaseOrderSearchCriteria{
    private PaymentType paymentType;
    private LocalDateTime approveAtFrom;
    private LocalDateTime approveAtTo;
    private String createdByName;
    private String approvedByName;
    private Integer createdById;
    private Integer approvedById;

    @Override
    public boolean isUserSpecific() {
        return false;
    }
}
