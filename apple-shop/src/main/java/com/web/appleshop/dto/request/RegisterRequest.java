package com.web.appleshop.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @NotBlank(message = "Họ không được để trống.")
    @Length(max = 50, message = "Họ phải có độ dài tối đa là 50 kí tự.")
    String firstName;

    @NotBlank(message = "Tên không được để trống.")
    @Length(max = 50, message = "Tên phải có độ dài tối đa là 50 kí tự.")
    String lastName;

    @NotNull(message = "Ngày sinh không được là giá trị null.")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ.")
    LocalDate birth;

    @NotBlank(message = "Tên đăng nhập không được để trống.")
    @Length(min = 5, max = 155, message = "Tên đăng nhập phải có độ dài từ 5 đến 155 kí tự.")
    String username;

    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Email không đúng định dạng.")
    @Length(min = 5, max = 255, message = "Email phải có độ dài từ 5 đến 255 kí tự.")
    String email;

    @Pattern(regexp = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$", message = "Số điện thoại không đúng định dạng")
    String phone;

    @NotBlank(message = "Mật khẩu không được để trống.")
    @Length(min = 8, max = 255, message = "Mật khẩu phải có độ dài từ 8 đến 255 kí tự.")
    String password;
}
