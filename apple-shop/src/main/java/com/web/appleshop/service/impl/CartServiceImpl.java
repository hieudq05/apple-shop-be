package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.AddCartItemRequest;
import com.web.appleshop.entity.CartItem;
import com.web.appleshop.entity.Stock;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.CartItemRepository;
import com.web.appleshop.repository.StockRepository;
import com.web.appleshop.service.CartService;
import com.web.appleshop.exception.IllegalArgumentException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);
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
        validateQuantity(stock, cartItem, cartItemRequest.getQuantity() + cartItem.getQuantity());

        cartItem.setQuantity(cartItemRequest.getQuantity() + cartItem.getQuantity());
        return cartItemRepository.save(cartItem);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @Transactional
    public CartItem updateCartItem(Integer cartItemId, Integer quantity) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CartItem cartItem = cartItemRepository.findCartItemByIdAndUserId(cartItemId, user.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm trong giỏ hàng."));
        if (quantity == 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }
        validateQuantity(cartItem.getStock(), cartItem, quantity);
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @Transactional
    public void deleteCartItem(Integer cartItemId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CartItem cartItem = cartItemRepository.findCartItemByIdAndUserId(cartItemId, user.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm trong giỏ hàng."));

        try {
            cartItemRepository.delete(cartItem);
        } catch (DataAccessException e) {
            log.warn(e.getMessage());
            throw new BadRequestException("Xảy ra lỗi khi xóa sản phẩm trong giỏ hàng. Vui lòng thử lại.");
        }
    }

    private void validateQuantity(Stock stock, CartItem cartItem, Integer requestQuantity) {
        if (stock.getQuantity() < requestQuantity) {
            throw new IllegalArgumentException("Số lượng sản phẩm trong kho không đủ.");
        }
        if (requestQuantity > 10) {
            throw new IllegalArgumentException("Bạn chỉ có thể thêm tối đa 10 sản phẩm trong giỏ hàng. Liên hệ với chúng tôi để đặt hàng với số lượng lớn hơn.");
        }
    }
}
