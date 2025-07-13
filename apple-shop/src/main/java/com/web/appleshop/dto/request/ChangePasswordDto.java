package com.web.appleshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {
    @NotBlank(message = "Mật khẩu cũ không được bỏ trống.")
    @Length(min = 6, max = 255, message = "Mật khẩu cũ phải có độ dài từ 6 đến 255 ký tự.")
    private String oldPassword;

    @NotBlank(message = "Mật khẩu mới không được bỏ trống.")
    @Length(min = 6, max = 255, message = "Mật khẩu mới phải có độ dài từ 6 đến 255 ký tự.")
    private String newPassword;

    @NotBlank(message = "Nhập lại mật khẩu mới.")
    @Length(min = 6, max = 255, message = "Mật khẩu mới phải có độ dài từ 6 đến 255 ký tự.")
    private String confirmPassword;
}
