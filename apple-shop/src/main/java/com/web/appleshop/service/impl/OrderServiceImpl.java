package com.web.appleshop.service.impl;

import com.web.appleshop.dto.PaymentDto;
import com.web.appleshop.dto.projection.OrderSummaryProjection;
import com.web.appleshop.dto.request.AdminCreateOrderRequest;
import com.web.appleshop.dto.request.UserCreateOrderRequest;
import com.web.appleshop.dto.response.OrderUserResponse;
import com.web.appleshop.dto.response.admin.OrderAdminResponse;
import com.web.appleshop.entity.*;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.CartItemRepository;
import com.web.appleshop.repository.OrderRepository;
import com.web.appleshop.repository.StockRepository;
import com.web.appleshop.repository.UserRepository;
import com.web.appleshop.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final UserService userService;
    private final OrderStatusManager orderStatusManager;
    private final MailService mailService;
    private final StockService stockService;
    private final VnPayService vnPayService;
    private final UserRepository userRepository;

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

            cartItem.getStock().setQuantity(cartItem.getStock().getQuantity() - cartItem.getQuantity());
            orderDetail.setStock(cartItem.getStock());
            stockRepository.save(cartItem.getStock());

            totalPrice = totalPrice.add(cartItem.getStock().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            cartItemRepository.delete(cartItem);
        }
        order.setOrderDetails(orderDetails);

        return orderRepository.save(order);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public List<Order> createOrder(AdminCreateOrderRequest[] orderRequests, PaymentType paymentType) {
        log.info("Bắt đầu tạo {} đơn hàng", orderRequests.length);

        List<Order> orders = new ArrayList<>();
        Map<Integer, User> userCache = userRepository.findAllByIdIn(
                Arrays.stream(orderRequests).map(AdminCreateOrderRequest::getCreatedByUserId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(u -> ((User) u).getId(), u -> (User) u));

        for (AdminCreateOrderRequest orderRequest : orderRequests) {
            User user = userCache.get(orderRequest.getCreatedByUserId());
            if (user == null) {
                log.error("Không tìm thấy người dùng với ID: {}", orderRequest.getCreatedByUserId());
                continue; // Bỏ qua đơn hàng này
            }

            Order order = new Order();
            order.setCreatedBy(user);
            order.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            order.setStatus(orderRequest.getStatus());
            order.setPaymentType(paymentType);
            order.setFirstName(orderRequest.getCustomInfo().getFirstName());
            order.setLastName(orderRequest.getCustomInfo().getLastName());
            order.setEmail(orderRequest.getCustomInfo().getEmail());
            order.setPhone(orderRequest.getCustomInfo().getPhone());
            order.setAddress(orderRequest.getCustomInfo().getAddress());
            order.setWard(orderRequest.getCustomInfo().getWard());
            order.setDistrict(orderRequest.getCustomInfo().getDistrict());
            order.setProvince(orderRequest.getCustomInfo().getProvince());
            order.setCountry("Việt Nam");
            orderRepository.save(order);

            Set<OrderDetail> orderDetails = new LinkedHashSet<>();
            for (AdminCreateOrderRequest.OrderDetailRequest orderDetailRequest : orderRequest.getOrderDetails()) {
                Stock stock = stockRepository.findById(orderDetailRequest.getStockId()).orElseThrow(
                        () -> new NotFoundException("Không tìm thấy sản phẩm hoặc sản phẩm không có trong kho.")
                );
                if (stock.getQuantity() < orderDetailRequest.getQuantity()) {
                    log.error("Không đủ hàng hoặc không tìm thấy sản phẩm với ID: {}", orderDetailRequest.getStockId());
                    throw new IllegalArgumentException("Không đủ hàng hoặc không tìm thấy sản phẩm với ID:" + orderDetailRequest.getStockId() + ".");
                }
                stock.setQuantity(stock.getQuantity() - orderDetailRequest.getQuantity());
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setStock(stock);
                orderDetail.setProduct(stock.getProduct());
                orderDetail.setProductName(stock.getProduct().getName());
                orderDetail.setQuantity(orderDetailRequest.getQuantity());
                orderDetail.setPrice(stock.getPrice());
                orderDetail.setColorName(stock.getColor().getName());
                orderDetail.setVersionName(stock.getInstanceProperties().stream().map(
                        InstanceProperty::getName
                ).collect(Collectors.joining(", ")));
                orderDetail.setImageUrl(stock.getProductPhotos().stream().findFirst().get().getImageUrl());
                orderDetails.add(orderDetail);
            }
            order.setOrderDetails(orderDetails);
            orders.add(order);
        }
        return orderRepository.saveAll(orders);
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Page<OrderSummaryProjection> getOrdersSummaryForAdmin(Pageable pageable) {
        return orderRepository.findAllBy(pageable);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_USER')")
    @Transactional
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

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Order cancelOrder(Integer orderId) {
        Order order = orderRepository.findOrderById(orderId).orElseThrow(() -> new BadRequestException("Order not found with id: " + orderId));
        OrderStatus oldStatus = order.getStatus();
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BadRequestException("Không thể chuyển đổi trạng thái " + order.getStatus() + " sang trạng thái " + OrderStatus.CANCELLED);
        }
        if (!orderStatusManager.isValidTransition(order.getStatus(), OrderStatus.CANCELLED)) {
            throw new BadRequestException("Không thể huỷ đơn hàng với trạng thái " + order.getStatus() + ".");
        }
        order.setStatus(OrderStatus.CANCELLED);

        Map<Integer, Integer> stockIdQuantityMap = new HashMap<>();
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            stockIdQuantityMap.put(orderDetail.getStock().getId(), orderDetail.getQuantity());
        }
        stockService.refundedStocks(stockIdQuantityMap);

        String mailSubject = "Bạn đã huỷ đơn hàng #" + (orderId);
        mailService.sendUpdateOrderStatusMail(order.getEmail(), mailSubject, OrderStatus.CANCELLED, orderId, oldStatus);

        return orderRepository.save(order);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @Transactional
    public Order cancelOrderForUser(Integer orderId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderRepository.findOrderById(orderId).orElseThrow(
                () -> new NotFoundException("Order not found with id: " + orderId)
        );
        OrderStatus oldStatus = order.getStatus();
        if (!Objects.equals(order.getCreatedBy().getId(), user.getId())) {
            throw new BadRequestException("Không tồn tại đơn hàng mã: " + orderId);
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BadRequestException("Không thể chuyển đổi trạng thái " + order.getStatus() + " sang trạng thái " + OrderStatus.CANCELLED);
        }
        if (!orderStatusManager.isValidTransition(order.getStatus(), OrderStatus.CANCELLED)) {
            throw new BadRequestException("Không thể huỷ đơn hàng với trạng thái " + order.getStatus() + ".");
        }
        order.setStatus(OrderStatus.CANCELLED);

        Map<Integer, Integer> stockIdQuantityMap = new HashMap<>();
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            stockIdQuantityMap.put(orderDetail.getStock().getId(), orderDetail.getQuantity());
        }
        stockService.refundedStocks(stockIdQuantityMap);

        String mailSubject = "Bạn đã huỷ đơn hàng #" + (orderId);
        mailService.sendUpdateOrderStatusMail(order.getEmail(), mailSubject, OrderStatus.CANCELLED, orderId, oldStatus);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Integer orderId) {
        return orderRepository.findOrderById(orderId).orElseThrow(
                () -> new NotFoundException("Không tồn tại đơn hàng có mã #" + orderId)
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public PaymentDto.VnPayResponse createVNPAYPaymentUrl(Integer orderId, HttpServletRequest request) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BadRequestException("Đơn hàng không được thanh toán vì đã được thanh toán hoặc đã được hủy.");
        }
        String paymentUrl = vnPayService.createPaymentUrl(
                request,
                calculateTotalPrice(order.getOrderDetails()).longValue(),
                "Thanh toan don hang #" + order.getId()
        );

        return new PaymentDto.VnPayResponse(
                "00",
                "Tạo đường dẫn thanh toán thành công",
                paymentUrl
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public PaymentDto.VnPayResponse createVNPAYPaymentUrlForUser(Integer orderId, HttpServletRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = getOrderById(orderId);
        if (!Objects.equals(order.getCreatedBy().getId(), user.getId())) {
            throw new BadRequestException("Không tìm thấy đơn hàng mã: " + orderId);
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BadRequestException("Đơn hàng không được thanh toán vì đã được thanh toán hoặc đã được hủy.");
        }
        String paymentUrl = vnPayService.createPaymentUrl(
                request,
                calculateTotalPrice(order.getOrderDetails()).longValue(),
                "Thanh toan don hang #" + order.getId()
        );

        return new PaymentDto.VnPayResponse(
                "00",
                "Tạo đường dẫn thanh toán thành công",
                paymentUrl
        );
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
