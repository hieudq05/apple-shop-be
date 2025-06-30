package com.web.appleshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDto {
    @NotBlank(message = "Không được bỏ trống Tên.")
    @Length(max = 1024, message = "Tên không được vượt quá 1024 ký tự.")
    String to_name;

    @NotBlank(message = "Không được bỏ trống số điện thoại.")
    String to_phone;

    @NotBlank(message = "Không được bỏ trống địa chỉ.")
    @Length(max = 1024, message = "Địa chỉ không được vượt quá 1024 ký tự.")
    String to_address;

    @NotBlank(message = "Không được bỏ trống xã.")
    @Length(max = 1024, message = "Xã không được vượt quá 1024 ký tự.")
    String to_ward_name;

    @NotBlank(message = "Không được bỏ trống quận.")
    @Length(max = 1024, message = "Quận không được vượt quá 1024 ký tự.")
    String to_district_name;

    @NotBlank(message = "Không được bỏ trống tỉnh.")
    @Length(max = 1024, message = "Tỉnh không được vượt quá 1024 ký tự.")
    String to_province_name;
}
