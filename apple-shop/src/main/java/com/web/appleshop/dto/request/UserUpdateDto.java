package com.web.appleshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.web.appleshop.entity.User}
 */
@Value
public class UserUpdateDto implements Serializable {
    @Pattern(regexp = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$", message = "Số điện thoại không đúng định dạng")
    String phone;

    @NotBlank(message = "Không được bỏ trống tên đệm")
    String firstName;

    @NotBlank(message = "Không được bỏ trống tên")
    String lastName;

    String image;

    @NotNull(message = "Không được để trống ngày sinh.")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ.")
    LocalDate birth;
}