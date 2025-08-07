package com.web.appleshop.dto.response.admin;

import com.web.appleshop.dto.response.OrderUserResponse;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
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
public class OrderAdminResponse implements Serializable {
    Integer id;
    ProductAdminResponse.ProductOwnerAdminResponse createdBy;
    LocalDateTime createdAt;
    PaymentType paymentType;
    LocalDateTime approveAt;
    ProductAdminResponse.ProductOwnerAdminResponse approveBy;
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
    String ward;
    String district;
    String province;
    String country;
    OrderStatus status;
    BigDecimal shippingDiscountAmount;
    BigDecimal productDiscountAmount;
    BigDecimal subTotal;
    BigDecimal shippingFee;
    BigDecimal finalTotal;
    BigDecimal vat;
    Set<OrderUserResponse.OrderDetailDto> orderDetails;
    String shippingTrackingCode;
}