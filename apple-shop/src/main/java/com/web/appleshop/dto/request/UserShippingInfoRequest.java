package com.web.appleshop.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserShippingInfoRequest {
    @Length(max = 55, message = "Họ phải có độ dài tối đa là 55 kí tự.")
    String firstName;

    @NotBlank(message = "Tên là bắt buộc.")
    @Length(max = 55, message = "Tên phải có độ dài tối đa là 55 kí tự.")
    String lastName;

    @NotBlank(message = "Email là bắt buộc.")
    @Length(max = 255, message = "Email phải có độ dài tối đa là 255 kí tự.")
    @Email(message = "Email không đúng định dạng.")
    String email;

    @NotBlank(message = "Số điện thoại là bắt buộc.")
    @Length(max = 20, message = "Số điện thoại phải có độ dài tối đa là 20 kí tự.")
    String phone;

    @Length(max = 500, message = "Địa chỉ phải có độ dài tối đa là 500 kí tự.")
    String address;

    @NotBlank(message = "Tỉnh là bắt buộc.")
    @Length(max = 100, message = "Tỉnh phải có độ dài tối đa là 100 kí tự.")
    String province;

    @NotBlank(message = "Phường là bắt buộc.")
    @Length(max = 100, message = "Quận phải có độ dài tối đa là 100 kí tự.")
    String ward;

    @NotBlank(message = "Huyện là bắt buộc")
    @Length(max = 100, message = "Huyện phải có độ dài tối đa là 100 kí tự.")
    String district;

    @NotNull(message = "Tuỳ chọn mặc định là bắt buộc.")
    Boolean isDefault;
}
