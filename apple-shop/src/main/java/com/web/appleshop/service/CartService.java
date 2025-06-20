package com.web.appleshop.service;

import com.web.appleshop.dto.request.AddCartItemRequest;
import com.web.appleshop.entity.CartItem;

public interface CartService {
    CartItem addCartItem(AddCartItemRequest cartItemRequest);
    CartItem updateCartItem(Integer cartItemId, Integer quantity);
}
