package com.web.appleshop.dto.response;

import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.Order}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderDetailResponse {
    Integer id;
    LocalDateTime createdAt;
    PaymentType paymentType;
    OrderStatus status;
    Set<OrderUserResponse.OrderDetailDto> orderDetails;
    String shippingTrackingCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String ward;
    private String district;
    private String province;
    private String country;
    private PromotionProductDto productProductPromotion;
    private PromotionShippingDto shippingShippingPromotion;
    private BigDecimal shippingDiscountAmount;
    private BigDecimal productDiscountAmount;
    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal finalTotal;
    private BigDecimal vat;


    /**
     * DTO for {@link com.web.appleshop.entity.OrderDetail}
     */
    @Data
    @AllArgsConstructor
    public static class OrderDetailDto implements Serializable {
        Integer id;
        OrderUserResponse.OrderDetailDto.ProductDto product;
        String productName;
        Integer quantity;
        BigDecimal price;
        String note;
        String colorName;
        String versionName;
        String image_url;

        /**
         * DTO for {@link com.web.appleshop.entity.Product}
         */
        @Data
        @AllArgsConstructor
        public static class ProductDto implements Serializable {
            Integer id;
        }
    }

    /**
     * DTO for {@link com.web.appleshop.entity.Promotion}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PromotionShippingDto implements Serializable {
        private Integer id;
        private String name;
        private String code;
    }

    /**
     * DTO for {@link com.web.appleshop.entity.Promotion}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PromotionProductDto implements Serializable {
        private Integer id;
        private String name;
        private String code;
    }
}
