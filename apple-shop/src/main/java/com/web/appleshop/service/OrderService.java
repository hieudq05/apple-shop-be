package com.web.appleshop.service;

import com.web.appleshop.dto.request.UserCreateOrderRequest;
import com.web.appleshop.dto.response.OrderUserResponse;
import com.web.appleshop.entity.Order;
import com.web.appleshop.entity.OrderDetail;
import com.web.appleshop.enums.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Set;

public interface OrderService {
    Order createOrder(UserCreateOrderRequest orderRequest, PaymentType paymentType);

    BigDecimal calculateTotalPrice(Set<OrderDetail> orderDetails);

    Page<OrderUserResponse> getOrdersForUser(Pageable pageable);
}
