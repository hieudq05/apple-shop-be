package com.web.appleshop.service;

import com.web.appleshop.enums.OrderStatus;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Component
public class OrderStatusManager {

    // Map lưu trữ các luồng trạng thái hợp lệ.
    private final Map<OrderStatus, Set<OrderStatus>> validTransitions = new EnumMap<>(OrderStatus.class);

    @PostConstruct
    private void initializeTransitions() {
        // Chờ thanh toán (PENDING) -> ...
        validTransitions.put(OrderStatus.PENDING_PAYMENT, Set.of(
                OrderStatus.FAILED_PAYMENT,
                OrderStatus.PAID,
                OrderStatus.CANCELLED
        ));

        // Đã thanh toán (PAID) -> ...
        validTransitions.put(OrderStatus.PAID, Set.of(
                OrderStatus.PROCESSING
        ));

        // Đang xử lý (PROCESSING) -> ...
        validTransitions.put(OrderStatus.PROCESSING, Set.of(
                OrderStatus.AWAITING_SHIPMENT
        ));

        // Đang chờ vận chuyển (AWAITING_SHIPMENT) -> ...
        validTransitions.put(OrderStatus.AWAITING_SHIPMENT, Set.of(
                OrderStatus.SHIPPED
        ));

        // Đang vận chuyển (SHIPPED) -> ...
        validTransitions.put(OrderStatus.SHIPPED, Set.of(
                OrderStatus.DELIVERED
        ));

        // Các trạng thái cuối (không thể chuyển đi đâu nữa)
        validTransitions.put(OrderStatus.DELIVERED, Collections.emptySet());
        validTransitions.put(OrderStatus.CANCELLED, Collections.emptySet());
        validTransitions.put(OrderStatus.FAILED_PAYMENT, Collections.emptySet());
        validTransitions.put(OrderStatus.REFUNDED, Collections.emptySet());
    }

    /**
     * Kiểm tra xem việc chuyển từ trạng thái hiện tại sang trạng thái mới có hợp lệ không.
     *
     * @param currentStatus Trạng thái hiện tại của đơn hàng.
     * @param newStatus     Trạng thái mới muốn chuyển đến.
     * @return true nếu hợp lệ, false nếu không.
     */
    public boolean isValidTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        Set<OrderStatus> allowedNextStates = validTransitions.get(currentStatus);

        if (allowedNextStates == null) {
            return false;
        }

        return allowedNextStates.contains(newStatus);
    }
}
