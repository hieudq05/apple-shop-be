package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.AddCartItemRequest;
import com.web.appleshop.dto.response.CartItemResponse;
import com.web.appleshop.dto.response.ProductUserResponse;
import com.web.appleshop.entity.CartItem;
import com.web.appleshop.entity.Promotion;
import com.web.appleshop.entity.Stock;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.exception.IllegalArgumentException;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.CartItemRepository;
import com.web.appleshop.repository.StockRepository;
import com.web.appleshop.service.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
            throw new BadRequestException("Xảy ra lỗi khi xóa sản phẩm trong giỏ hàng. Vui lòng thử lại.");
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @Transactional
    @Override
    public void deleteAllCartItems() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            cartItemRepository.deleteAllByUserId(user.getId());
        } catch (DataAccessException e) {
            throw new BadRequestException("Xảy ra lỗi khi xóa sản phẩm trong giỏ hàng. Vui lòng thử lại.");
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public Page<CartItemResponse> getCartItems(Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return cartItemRepository.findCartItemsByUserId(user.getId(), pageable).map(this::convertCartItemToCartItemResponse);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    private CartItemResponse convertCartItemToCartItemResponse(CartItem cartItem) {
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setId(cartItem.getId());
        cartItemResponse.setQuantity(cartItem.getQuantity());

        Set<CartItemResponse.ProductDto.PromotionDto> promotions = new LinkedHashSet<>();
        for (Promotion promotion: cartItem.getProduct().getPromotions()) {
            promotions.add(
                    new CartItemResponse.ProductDto.PromotionDto(
                            promotion.getId(),
                            promotion.getName(),
                            promotion.getCode(),
                            new CartItemResponse.ProductDto.PromotionDto.PromotionTypeDto(
                                    promotion.getPromotionType().getId(),
                                    promotion.getPromotionType().getName()
                            ),
                            promotion.getValue(),
                            promotion.getIsActive(),
                            promotion.getStartDate(),
                            promotion.getEndDate()
                    )
            );
        }

        CartItemResponse.ProductDto productDto = new CartItemResponse.ProductDto(
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getDescription(),
                promotions
        );

        CartItemResponse.StockDto stockDto = new CartItemResponse.StockDto(
                cartItem.getStock().getId(),
                new ProductUserResponse.ProductStockResponse.StockColorResponse(
                        cartItem.getStock().getColor().getId(),
                        cartItem.getStock().getColor().getName(),
                        cartItem.getStock().getColor().getHexCode()
                ),
                cartItem.getStock().getQuantity(),
                cartItem.getStock().getPrice(),
                cartItem.getStock().getProductPhotos().stream().map(photo -> new ProductUserResponse.ProductStockResponse.StockPhotoResponse(photo.getId(), photo.getImageUrl(), photo.getAlt())).collect(Collectors.toSet()),
                cartItem.getStock().getInstanceProperties().stream().map(instance -> new ProductUserResponse.ProductStockResponse.StockInstanceResponse(instance.getId(), instance.getName())).collect(Collectors.toSet())
        );

        cartItemResponse.setProduct(productDto);
        cartItemResponse.setStock(stockDto);
        return cartItemResponse;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    private void validateQuantity(Stock stock, CartItem cartItem, Integer requestQuantity) {
        if (stock.getQuantity() < requestQuantity) {
            throw new IllegalArgumentException("Số lượng sản phẩm trong kho không đủ.");
        }
        if (requestQuantity > 10) {
            throw new IllegalArgumentException("Bạn chỉ có thể thêm tối đa 10 sản phẩm trong giỏ hàng. Liên hệ với chúng tôi để đặt hàng với số lượng lớn hơn.");
        }
    }
}
