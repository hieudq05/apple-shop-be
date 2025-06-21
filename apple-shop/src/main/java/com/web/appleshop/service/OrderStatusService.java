package com.web.appleshop.service;

import com.web.appleshop.entity.Order;
import com.web.appleshop.enums.OrderStatus;

public interface OrderStatusService {
    Order updateStatus(Integer orderId, OrderStatus newStatus);
}
