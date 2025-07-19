package com.web.appleshop.service;

import com.web.appleshop.dto.PaymentDto;
import com.web.appleshop.dto.projection.OrderSummaryProjection;
import com.web.appleshop.dto.request.*;
import com.web.appleshop.dto.response.OrderUserResponse;
import com.web.appleshop.dto.response.UserOrderDetailResponse;
import com.web.appleshop.dto.response.admin.OrderAdminResponse;
import com.web.appleshop.dto.response.admin.OrderSummaryV2Dto;
import com.web.appleshop.dto.response.statistics.OrderTotalRevenue;
import com.web.appleshop.entity.Order;
import com.web.appleshop.entity.OrderDetail;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface OrderService {
    Order createOrder(UserCreateOrderRequest orderRequest, PaymentType paymentType);

    List<Order> createOrder(AdminCreateOrderRequest[] orderRequests);

    BigDecimal calculateTotalPrice(Set<OrderDetail> orderDetails);

    Page<OrderUserResponse> getOrdersForUser(Pageable pageable);

    UserOrderDetailResponse getOrderDetailByIdForUser(Integer id);

    Page<OrderUserResponse> searchOrdersForUser(UserOrderSearchCriteria criteria, Pageable pageable);

    OrderAdminResponse getOrderDetailByIdForAdmin(Integer id);

    Page<OrderSummaryProjection> getOrdersSummaryForAdmin(Pageable pageable);

    Page<OrderSummaryV2Dto> searchOrdersSummaryForAdmin(AdminOrderSearchCriteria criteria, Pageable pageable);

    Order updateOrderStatus(Integer orderId, OrderStatus status);

    Order cancelOrder(Integer orderId);

    Order cancelOrderForUser(Integer orderId);

    Order getOrderById(Integer orderId);

    PaymentDto.VnPayResponse createVNPAYPaymentUrl(Integer orderId, HttpServletRequest request);

    PaymentDto.VnPayResponse createVNPAYPaymentUrlForUser(Integer orderId, HttpServletRequest request);

    PaymentDto.PayPalResponse createPaypalPaymentUrl(Integer orderId, HttpServletRequest request);

    Order createOrderWithPromotion(UserCreateOrderWithPromotionRequest orderRequest, PaymentType paymentType);

    Order createOrderWithPromotionForAdmin(AdminCreateOrderRequest orderRequest);

    /**
     * Statistics
     */
    BigDecimal getOrderTotalRevenue(OrderStatus status, LocalDateTime fromDate, LocalDateTime toDate);

    BigDecimal getAllOrderTotalRevenue(LocalDateTime fromDate, LocalDateTime toDate);

    Long getNumberOfOrders(OrderStatus status, LocalDateTime fromDate, LocalDateTime toDate);

    Long getAllNumberOfOrders(LocalDateTime fromDate, LocalDateTime toDate);

    Long getNumberOfProductsSold(LocalDateTime fromDate, LocalDateTime toDate);
}
