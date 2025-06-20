package com.web.appleshop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @NotNull(message = "Không được bỏ trống số lượng sản phẩm.")
    @Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn 0.")
    Integer quantity;

}
