package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.UserCreateOrderRequest;
import com.web.appleshop.entity.CartItem;
import com.web.appleshop.entity.Order;
import com.web.appleshop.entity.OrderDetail;
import com.web.appleshop.entity.User;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.repository.CartItemRepository;
import com.web.appleshop.repository.OrderDetailRepository;
import com.web.appleshop.repository.OrderRepository;
import com.web.appleshop.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @Transactional
    public Order createOrder(UserCreateOrderRequest orderRequest, PaymentType paymentType) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CartItem> cartItems = cartItemRepository.findCartItemsByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Không có sản phẩm trong giỏ hàng.");
        }

        Order order = new Order();
        BeanUtils.copyProperties(orderRequest, order);
        order.setCreatedBy(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setPaymentType(paymentType);

        BigDecimal totalPrice = BigDecimal.ZERO;
        Set<OrderDetail> orderDetails = new LinkedHashSet<>();
        for (CartItem cartItem : cartItems) {
            if (cartItem.getStock().getQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Số lượng sản phẩm trong kho không đủ.");
            }

            if (cartItem.getQuantity() == 0) {
                throw new IllegalArgumentException("Số lượng sản phẩm không hợp lệ.");
            }

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setProductName(cartItem.getProductName());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getStock().getPrice());
            orderDetail.setColorName(cartItem.getStock().getColor().getName());
            orderDetail.setVersionName(cartItem.getStock().getInstance().getName());
            orderDetails.add(orderDetail);

            totalPrice = totalPrice.add(cartItem.getStock().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            cartItemRepository.delete(cartItem);
        }
        order.setOrderDetails(orderDetails);

        return orderRepository.save(order);
    }

    @Override
    public BigDecimal calculateTotalPrice(Set<OrderDetail> orderDetails) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderDetail detail : orderDetails) {
            totalPrice = totalPrice.add(detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
        }
        return totalPrice;
    }
}
