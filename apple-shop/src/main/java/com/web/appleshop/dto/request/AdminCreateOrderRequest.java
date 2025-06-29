package com.web.appleshop.dto.request;

import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminCreateOrderRequest {
    @NotNull(message = "Không được bỏ trống mã người dùng.")
    Integer createdByUserId;

    @NotNull(message = "Không được bỏ trống phương thức thanh toán.")
    PaymentType paymentType;

    @NotNull(message = "Không được bỏ trống trạng thái đơn hàng.")
    OrderStatus status;

    @NotNull(message = "Không được bỏ trống mã thông tin giao hàng.")
    CustomInfoRequest customInfo;

    @NotEmpty(message = "Không được bỏ trống chi tiết đơn hàng.")
    OrderDetailRequest[] orderDetails;

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
