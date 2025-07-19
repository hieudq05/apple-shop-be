package com.web.appleshop.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateOrderWithPromotionRequest {
    @NotBlank(message = "Họ không được để trống")
    @Size(max = 55, message = "Họ không được vượt quá 55 ký tự")
    private String firstName;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 55, message = "Tên không được vượt quá 55 ký tự")
    private String lastName;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10-11 chữ số")
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 500, message = "Địa chỉ không được vượt quá 500 ký tự")
    private String address;

    @NotBlank(message = "Phường/Xã không được để trống")
    @Size(max = 100, message = "Phường/Xã không được vượt quá 100 ký tự")
    private String ward;

    @NotBlank(message = "Quận/Huyện không được để trống")
    @Size(max = 100, message = "Quận/Huyện không được vượt quá 100 ký tự")
    private String district;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Size(max = 100, message = "Tỉnh/Thành phố không được vượt quá 100 ký tự")
    private String province;

    @Size(max = 50, message = "Mã giảm giá sản phẩm không được vượt quá 50 ký tự")
    private String productPromotionCode;

    @Size(max = 50, message = "Mã giảm giá vận chuyển không được vượt quá 50 ký tự")
    private String shippingPromotionCode;
}
