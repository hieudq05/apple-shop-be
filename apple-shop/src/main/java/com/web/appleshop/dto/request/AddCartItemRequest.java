package com.web.appleshop.dto.request;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.web.appleshop.entity.CartItem}
 */
@Value
public class AddCartItemRequest implements Serializable {
    Integer stockId;
    Integer quantity;
}