package com.web.appleshop.dto.request;

import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminCreateOrderRequest {
    Integer createdByUserId;

    @NotNull(message = "Không được bỏ trống phương thức thanh toán.")
    PaymentType paymentType;

    @NotNull(message = "Không được bỏ trống trạng thái đơn hàng.")
    OrderStatus status;

    @NotNull(message = "Không được bỏ trống mã thông tin giao hàng.")
    CustomInfoRequest customInfo;

    @NotEmpty(message = "Không được bỏ trống chi tiết đơn hàng.")
    OrderDetailRequest[] orderDetails;

    @Size(max = 50, message = "Mã giảm giá sản phẩm không được vượt quá 50 ký tự")
    private String productPromotionCode;

    @Size(max = 50, message = "Mã giảm giá vận chuyển không được vượt quá 50 ký tự")
    private String shippingPromotionCode;

    @DecimalMin(value = "0", message = "Phí vận chuyển phải >= 0")
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Value
    public static class OrderDetailRequest {
        Integer stockId;
        Integer quantity;
    }

    @Value
    public static class CustomInfoRequest {
        String firstName;
        String lastName;
        String email;
        String phone;
        String address;
        String ward;
        String district;
        String province;
    }
}
