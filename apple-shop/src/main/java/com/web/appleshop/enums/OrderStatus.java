package com.web.appleshop.enums;

public enum OrderStatus {
    // Khách hàng vừa đặt hàng, đang chờ thanh toán
    PENDING_PAYMENT,

    // Thanh toán thất bại
    FAILED_PAYMENT,

    // Khách hàng đã thanh toán thành công
    PAID,

    // Đơn hàng đang được cửa hàng xử lý, chuẩn bị hàng
    PROCESSING,

    // Đơn hàng đã được đóng gói, chờ đơn vị vận chuyển đến lấy
    AWAITING_SHIPMENT,

    // Đơn vị vận chuyển đã lấy hàng và đang trên đường giao
    SHIPPED,

    // Giao hàng thành công
    DELIVERED,

    // Khách hàng hoặc cửa hàng đã hủy đơn
    CANCELLED,

    // Đơn hàng yêu cầu trả lại/hoàn tiền
    RETURN_REQUESTED,

    // Đơn hàng đã được hoàn tiền
    REFUNDED
}
