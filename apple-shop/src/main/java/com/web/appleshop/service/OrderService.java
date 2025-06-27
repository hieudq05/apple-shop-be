package com.web.appleshop.service;

import com.web.appleshop.dto.projection.OrderSummaryProjection;
import com.web.appleshop.dto.request.UserCreateOrderRequest;
import com.web.appleshop.dto.response.OrderUserResponse;
import com.web.appleshop.dto.response.admin.OrderAdminResponse;
import com.web.appleshop.dto.response.admin.OrderSummaryDto;
import com.web.appleshop.entity.Order;
import com.web.appleshop.entity.OrderDetail;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Set;

public interface OrderService {
    Order createOrder(UserCreateOrderRequest orderRequest, PaymentType paymentType);

    BigDecimal calculateTotalPrice(Set<OrderDetail> orderDetails);

    Page<OrderUserResponse> getOrdersForUser(Pageable pageable);

    OrderAdminResponse getOrderDetailByIdForAdmin(Integer id);

    Page<OrderSummaryProjection> getOrdersSummaryForAdmin(Pageable pageable);

    Order updateOrderStatus(Integer orderId, OrderStatus status);
}
