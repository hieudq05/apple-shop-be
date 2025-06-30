package com.web.appleshop.service.impl;

import com.web.appleshop.entity.Order;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.exception.InvalidStatusTransitionException;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.OrderRepository;
import com.web.appleshop.service.OrderStatusManager;
import com.web.appleshop.service.OrderStatusService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusImpl implements OrderStatusService {
    private final OrderRepository orderRepository;
    private final OrderStatusManager orderStatusManager;

    public OrderStatusImpl(OrderRepository orderRepository, OrderStatusManager orderStatusManager) {
        this.orderRepository = orderRepository;
        this.orderStatusManager = orderStatusManager;
    }

    @Override
    @Transactional
    public Order updateStatus(Integer orderId, OrderStatus newStatus) {
        Order order = orderRepository.findOrderById(orderId).orElseThrow(
                () -> new NotFoundException("Không tìm thấy đơn hàng với id: " + orderId)
        );
        if (!orderStatusManager.isValidTransition(order.getStatus(), newStatus)) {
            throw new InvalidStatusTransitionException("Không thể chuyển trạng thái đơn hàng từ "
                    + order.getStatus() + " sang " + newStatus + ".");
        }
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

}
