package com.web.appleshop.service;

public interface OrderService {
    void cancelOrder(Integer orderId, String reason);
}
