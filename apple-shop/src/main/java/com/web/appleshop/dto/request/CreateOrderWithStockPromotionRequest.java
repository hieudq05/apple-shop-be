package com.web.appleshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderWithStockPromotionRequest {
    @NotNull(message = "Shipping address is required")
    private ShippingAddress shippingAddress;

    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItem> orderItems;

    private String notes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShippingAddress {
        @NotNull(message = "Full name is required")
        private String fullName;

        @NotNull(message = "Phone number is required")
        private String phoneNumber;

        @NotNull(message = "Address is required")
        private String address;

        @NotNull(message = "City is required")
        private String city;

        @NotNull(message = "District is required")
        private String district;

        @NotNull(message = "Ward is required")
        private String ward;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItem {
        @NotNull(message = "Stock ID is required")
        private Integer stockId; // Sử dụng stockId thay vì productId

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        @NotNull(message = "Unit price is required")
        private BigDecimal unitPrice;

        // Promotion được chọn cho stock này (user chọn từ frontend)
        private String selectedPromotionCode;
    }
}
