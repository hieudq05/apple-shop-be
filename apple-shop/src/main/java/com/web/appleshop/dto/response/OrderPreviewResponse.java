package com.web.appleshop.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderPreviewResponse {
    private List<OrderItemPreview> items;
    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal totalPromotionDiscount;
    private BigDecimal shippingDiscount;
    private BigDecimal totalDiscount;
    private BigDecimal finalTotal;
    private List<AppliedPromotion> appliedPromotions;
    private List<String> warnings; // Promotion warnings/info
    private boolean canProceed;
    private String message;

    @Data
    public static class OrderItemPreview {
        private Integer productId;
        private String productName;
        private String imageUrl;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
        private BigDecimal totalDiscountAmount;
        private BigDecimal finalPrice;
        private List<AppliedPromotion> appliedPromotions;
    }

    @Data
    public static class AppliedPromotion {
        private String code;
        private String name;
        private String type;
        private BigDecimal value;
        private BigDecimal discountAmount;
        private String applyLevel; // "ORDER", "PRODUCT", "CATEGORY", "SHIPPING"
        private String source; // "PRODUCT", "USER_INPUT", "AUTO"
        private String description;
        private boolean success;
    }
}
