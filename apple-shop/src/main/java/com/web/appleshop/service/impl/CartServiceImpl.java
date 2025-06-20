package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.AddCartItemRequest;
import com.web.appleshop.entity.CartItem;
import com.web.appleshop.entity.Stock;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.CartItemRepository;
import com.web.appleshop.repository.StockRepository;
import com.web.appleshop.service.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final StockRepository stockRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @Override
    public CartItem addCartItem(AddCartItemRequest cartItemRequest) {
        CartItem cartItem;

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Stock stock = stockRepository.findById(cartItemRequest.getStockId()).orElseThrow(
                () -> new NotFoundException("Không tìm thấy sản phẩm hoặc sản phẩm không có trong kho.")
        );

        cartItem = cartItemRepository.findCartItemByUserIdAndStock_Id(user.getId(), stock.getId()).orElse(
                CartItem.builder()
                        .stock(stock)
                        .user(user)
                        .product(stock.getProduct())
                        .productName(stock.getProduct().getName())
                        .quantity(0)
                        .build()
        );
        if (stock.getQuantity() < cartItemRequest.getQuantity() + cartItem.getQuantity()) {
            throw new IllegalArgumentException("Số lượng sản phẩm trong kho không đủ.");
        } else if (cartItemRequest.getQuantity() + cartItem.getQuantity() > 10) {
            throw new IllegalArgumentException("Bạn chỉ có thể mua tối đa 10 sản phẩm trong một lần. Liên hệ với chúng tôi để đặt hàng với số lượng lớn hơn.");
        }

        cartItem.setQuantity(cartItemRequest.getQuantity() + cartItem.getQuantity());
        return cartItemRepository.save(cartItem);
    }
}
