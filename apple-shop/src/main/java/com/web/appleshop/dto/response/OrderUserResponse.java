package com.web.appleshop.dto.response;

import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.Order}
 */
@Value
public class OrderUserResponse implements Serializable {
    Integer id;
    LocalDateTime createdAt;
    PaymentType paymentType;
    OrderStatus status;
    BigDecimal finalTotal;
    Set<OrderDetailDto> orderDetails;
    String shippingTrackingCode;

    /**
     * DTO for {@link com.web.appleshop.entity.OrderDetail}
     */
    @Value
    public static class OrderDetailDto implements Serializable {
        Integer id;
        ProductDto product;
        String productName;
        Integer stockId;
        Integer quantity;
        BigDecimal price;
        String note;
        String colorName;
        String versionName;
        String image_url;
        Boolean isReviewed;

        /**
         * DTO for {@link com.web.appleshop.entity.Product}
         */
        @Value
        public static class ProductDto implements Serializable {
            Integer id;
        }
    }
}