package com.web.appleshop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.web.appleshop.entity.CartItem}
 */
@Value
public class AddCartItemRequest implements Serializable {
    @NotNull(message = "Không được bỏ trống kho sản phẩm.")
    Integer stockId;

    @NotNull(message = "Không được bỏ trống số lượng sản phẩm.")
    @Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn 0.")
    Integer quantity;
}