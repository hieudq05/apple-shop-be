package com.web.appleshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.Order}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateOrderRequest implements Serializable {
    @NotBlank(message = "Không được bỏ trống họ.")
    @Size(max = 55, message = "Họ không được vượt quá 55 ký tự.")
    String firstName;

    @NotBlank(message = "Không được bỏ trống tên.")
    @Size(max = 55, message = "Tên không được vượt quá 55 ký tự.")
    String lastName;

    @NotBlank(message = "Không được bỏ trống email.")
    @Email(message = "Email không đúng định dạng.")
    String email;

    @NotBlank(message = "Không được bỏ trống số điện thoại.")
    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự.")
    String phone;

    @NotBlank(message = "Không được bỏ trống địa chỉ.")
    @Size(max = 500, message = "Địa chỉ không được vượt quá 500 ký tự.")
    String address;

    @NotBlank(message = "Không được bỏ trống xã.")
    @Size(max = 100, message = "Xã không được vượt quá 100 ký tự.")
    String ward;

    @NotBlank(message = "Không được bỏ trống quận.")
    @Size(max = 100, message = "Quận không được vượt quá 100 ký tự.")
    String district;

    @NotBlank(message = "Không được bỏ trống tỉnh.")
    @Size(max = 100, message = "Tỉnh không được vượt quá 100 ký tự.")
    String province;

    @NotBlank(message = "Không được bỏ trống quốc gia.")
    @Size(max = 100, message = "Quốc gia không được vượt quá 100 ký tự.")
    String country;
}