package com.web.appleshop.service.impl;

import com.web.appleshop.dto.projection.OrderSummaryProjection;
import com.web.appleshop.dto.request.UserCreateOrderRequest;
import com.web.appleshop.dto.response.OrderUserResponse;
import com.web.appleshop.dto.response.admin.OrderAdminResponse;
import com.web.appleshop.dto.response.admin.OrderSummaryDto;
import com.web.appleshop.entity.*;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.repository.CartItemRepository;
import com.web.appleshop.repository.OrderRepository;
import com.web.appleshop.repository.StockRepository;
import com.web.appleshop.service.MailService;
import com.web.appleshop.service.OrderService;
import com.web.appleshop.service.OrderStatusManager;
import com.web.appleshop.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final UserService userService;
    private final OrderStatusManager orderStatusManager;
    private final MailService mailService;

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
            orderDetail.setVersionName(cartItem.getStock().getInstanceProperties().stream().map(
                    InstanceProperty::getName
            ).collect(Collectors.joining(", ")));
            orderDetail.setImageUrl(cartItem.getStock().getProductPhotos().stream().findFirst().get().getImageUrl());
            orderDetails.add(orderDetail);

            Stock stock = cartItem.getStock();
            if (stock.getQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException("Số lượng sản phẩm trong kho không đủ.");
            }
            stock.setQuantity(stock.getQuantity() - cartItem.getQuantity());
            stockRepository.save(stock);

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

    @Override
    public Page<OrderUserResponse> getOrdersForUser(Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Order> orders = orderRepository.findOrdersByCreatedBy(user, pageable);
        return orders.map(this::convertOrderToOrderUserResponse);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public OrderAdminResponse getOrderDetailByIdForAdmin(Integer id) {
        Order order = orderRepository.findOrderById(id).orElseThrow(() -> new BadRequestException("Order not found with id: " + id));
        return convertOrderToOrderAdminResponse(order);
    }

    @Override
    public Page<OrderSummaryProjection> getOrdersSummaryForAdmin(Pageable pageable) {
        return orderRepository.findAllBy(pageable);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Order updateOrderStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.findOrderById(orderId).orElseThrow(() -> new BadRequestException("Order not found with id: " + orderId));
        if (!orderStatusManager.isValidTransition(order.getStatus(), status)) {
            throw new BadRequestException("Trạng thái đích không hợp lệ!");
        }

        OrderStatus oldStatus = order.getStatus();

        order.setStatus(status);
        String mailSubject = "Cập nhật trạng thái đơn hàng #" + (orderId);
        mailService.sendUpdateOrderStatusMail(order.getEmail(), mailSubject, status, orderId, oldStatus);

        return orderRepository.save(order);
    }

    private OrderAdminResponse convertOrderToOrderAdminResponse(Order order) {
        return new OrderAdminResponse(
                order.getId(),
                userService.convertUserToProductOwnerAdminResponse(order.getCreatedBy()),
                order.getCreatedAt(),
                order.getPaymentType(),
                order.getApproveAt(),
                order.getApproveBy() == null ? null : userService.convertUserToProductOwnerAdminResponse(order.getApproveBy()),
                order.getFirstName(),
                order.getLastName(),
                order.getEmail(),
                order.getPhone(),
                order.getAddress(),
                order.getWard(),
                order.getDistrict(),
                order.getProvince(),
                order.getCountry(),
                order.getStatus(),
                order.getOrderDetails().stream().map(this::convertOrderDetailToOrderDetailDto).collect(Collectors.toSet()),
                order.getShippingTrackingCode()
        );
    }

    private OrderUserResponse convertOrderToOrderUserResponse(Order order) {
        return new OrderUserResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getPaymentType(),
                order.getStatus(),
                order.getOrderDetails().stream().map(this::convertOrderDetailToOrderDetailDto).collect(Collectors.toSet()),
                order.getShippingTrackingCode()
        );
    }

    private OrderUserResponse.OrderDetailDto convertOrderDetailToOrderDetailDto(OrderDetail orderDetail) {
        return new OrderUserResponse.OrderDetailDto(
                orderDetail.getId(),
                new OrderUserResponse.OrderDetailDto.ProductDto(orderDetail.getProduct().getId()),
                orderDetail.getProductName(),
                orderDetail.getQuantity(),
                orderDetail.getPrice(),
                orderDetail.getNote(),
                orderDetail.getColorName(),
                orderDetail.getVersionName(),
                orderDetail.getImageUrl()
        );
    }
}
