package com.web.appleshop.dto.request;

import com.web.appleshop.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseOrderSearchCriteria {
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String shippingAddress;
    private String province;
    private String district;
    private String ward;
    private String country;
    private String shippingTrackingCode;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
    private String searchTerm; // Tìm kiếm tổng quát
    private OrderStatus status;
    private String sortBy;
    private String sortDirection;

    public abstract boolean isUserSpecific();
}
