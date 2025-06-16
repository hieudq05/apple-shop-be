package com.web.appleshop.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    @NotBlank(message = "Vui lòng nhập email.")
    @Length(max = 255, message = "Email không được vượt quá 255 ký tự.")
    @Email(message = "Email không đúng định dạng.")
    String email;
    @NotBlank(message = "Vui lòng nhập mật khẩu.")
    @Length(min = 6, max = 255, message = "Mật khẩu phải có độ dài từ 6 đến 255 ký tự.")
    String password;
}
