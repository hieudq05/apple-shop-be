package com.web.appleshop.dto.request;

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
    @NotBlank(message = "Vui lòng nhập username, email hoặc số điện thoại.")
    @Length(max = 255, message = "Username, email hoặc số điện thoại không được vượt quá 255 ký tự.")
    String loginIdentifier;
    @NotBlank(message = "Vui lòng nhập mật khẩu.")
    @Length(min = 6, max = 255, message = "Mật khẩu phải có độ dài từ 6 đến 255 ký tự.")
    String password;
}
