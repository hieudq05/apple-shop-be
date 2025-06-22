package com.web.appleshop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.appleshop.entity.Order;
import com.web.appleshop.entity.OrderDetail;
import com.web.appleshop.entity.OrderStatus;
import com.web.appleshop.entity.Stock;
import com.web.appleshop.repository.OrderRepository;
import com.web.appleshop.repository.OrderStatusRepository;
import com.web.appleshop.repository.StockRepository;
import com.web.appleshop.service.OrderService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

// OrderServiceImpl.java
@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final StockRepository stockRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderStatusRepository orderStatusRepository,
                            StockRepository stockRepository) {
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    public void cancelOrder(Integer orderId, String reason) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng ID: " + orderId));

        // Validate order status
        validateOrderStatus(order);

        // Update order status
        updateOrderStatus(order, reason);

        // Restore stock quantities
        restoreStockQuantities(order);

        orderRepository.save(order);
    }

    private void validateOrderStatus(Order order) {
        if ("Đã hủy".equals(order.getStatus().getName())) {
            throw new IllegalStateException("Đơn hàng đã bị hủy trước đó");
        }
        
        if ("Đã giao".equals(order.getStatus().getName())) {
            throw new IllegalStateException("Không thể hủy đơn hàng đã giao");
        }
    }

    private void updateOrderStatus(Order order, String reason) {
        OrderStatus cancelledStatus = orderStatusRepository.findByName("Đã hủy")
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trạng thái 'Đã hủy'"));
        
        order.setStatus(cancelledStatus);
        order.setCancelReason(reason);
    }

    private void restoreStockQuantities(Order order) {
        for (OrderDetail detail : order.getOrderDetails()) {
            try {
                Stock stock = resolveStock(detail);
                stock.setQuantity(stock.getQuantity() + detail.getQuantity());
                stockRepository.save(stock);
            } catch (EntityNotFoundException ex) {
                log.error("Lỗi hoàn trả tồn kho: {}", ex.getMessage());
                throw ex;
            }
        }
    }

    private Stock resolveStock(OrderDetail detail) {
        // Case 1: Direct stock reference exists
        if (detail.getStock() != null) {
            return detail.getStock();
        }
        
        // Case 2: Find stock by product and color name
        return stockRepository.findByProductAndColorName(
                    detail.getProduct(), 
                    detail.getColorName()
                )
                .orElseThrow(() -> new EntityNotFoundException(
                    "Không tìm thấy tồn kho cho sản phẩm: " + detail.getProduct().getName() + 
                    ", màu: " + detail.getColorName()
                ));
    }
}