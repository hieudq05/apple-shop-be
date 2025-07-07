package com.web.appleshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ApplyPromotionRequest {
    @NotBlank(message = "Promotion code is required")
    private String promotionCode;

    @NotNull(message = "Order details are required")
    private List<OrderItem> orderItems;

    @Data
    public static class OrderItem {
        @NotNull(message = "Product ID is required")
        private Integer productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        @NotNull(message = "Unit price is required")
        private BigDecimal unitPrice;
    }
}
