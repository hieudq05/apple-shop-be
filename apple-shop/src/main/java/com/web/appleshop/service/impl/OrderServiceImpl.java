package com.web.appleshop.service.impl;

import com.web.appleshop.dto.PaymentDto;
import com.web.appleshop.dto.projection.OrderSummaryProjection;
import com.web.appleshop.dto.request.*;
import com.web.appleshop.dto.response.OrderUserResponse;
import com.web.appleshop.dto.response.UserOrderDetailResponse;
import com.web.appleshop.dto.response.admin.OrderAdminResponse;
import com.web.appleshop.dto.response.admin.OrderSummaryV2Dto;
import com.web.appleshop.entity.*;
import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.exception.IllegalArgumentException;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.CartItemRepository;
import com.web.appleshop.repository.OrderRepository;
import com.web.appleshop.repository.StockRepository;
import com.web.appleshop.repository.UserRepository;
import com.web.appleshop.service.*;
import com.web.appleshop.specification.OrderSpecification;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final OrderSpecification orderSpecification;
    private final PromotionService promotionService;

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
            orderDetail.setImageUrl(cartItem.getStock().getProductPhotos().stream()
                    .findFirst()
                    .map(ProductPhoto::getImageUrl)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy hình ảnh sản phẩm.")));
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
    public List<Order> createOrder(AdminCreateOrderRequest[] orderRequests) {
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
            order.setPaymentType(orderRequest.getPaymentType());
            order.setFirstName(orderRequest.getCustomInfo().getFirstName());
            order.setLastName(orderRequest.getCustomInfo().getLastName());
            order.setEmail(orderRequest.getCustomInfo().getEmail());
            order.setPhone(orderRequest.getCustomInfo().getPhone());
            order.setAddress(orderRequest.getCustomInfo().getAddress());
            order.setWard(orderRequest.getCustomInfo().getWard());
            order.setDistrict(orderRequest.getCustomInfo().getDistrict());
            order.setProvince(orderRequest.getCustomInfo().getProvince());
            order.setCountry("Việt Nam");

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
                orderDetail.setImageUrl(stock.getProductPhotos().stream()
                        .findFirst()
                        .map(ProductPhoto::getImageUrl)
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy hình ảnh sản phẩm.")));
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
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public Page<OrderUserResponse> getOrdersForUser(Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Order> orders = orderRepository.findOrdersByCreatedBy(user, pageable);
        return orders.map(this::convertOrderToOrderUserResponse);
    }

    @Override
    public UserOrderDetailResponse getOrderDetailByIdForUser(Integer id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderRepository.
                findOrderByIdAndCreatedBy(id, user).orElseThrow(
                        () -> new BadRequestException("Order not found with id: " + id)
                );
        return mapToUserOrderDetailResponse(order);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public Page<OrderUserResponse> searchOrdersForUser(UserOrderSearchCriteria criteria, Pageable pageable) {
        Specification<Order> spec = orderSpecification.buildSpecification(criteria);

        Page<Order> orders = orderRepository.findAll(spec, pageable);

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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Page<OrderSummaryV2Dto> searchOrdersSummaryForAdmin(AdminOrderSearchCriteria criteria, Pageable pageable) {
        Specification<Order> spec = orderSpecification.buildSpecification(criteria);
        Page<Order> orders = orderRepository.findAll(spec, pageable);
        return orders.map(this::convertOrderToOrderSummaryV2Dto);
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
        if (status == OrderStatus.PROCESSING) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            order.setApproveAt(LocalDateTime.now());
            order.setApproveBy(user);
        }
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
    @Transactional(readOnly = true)
    public Order getOrderById(Integer orderId) {
        return orderRepository.findOrderById(orderId).orElseThrow(
                () -> new NotFoundException("Không tồn tại đơn hàng có mã #" + orderId)
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public PaymentDto.VnPayResponse createVNPAYPaymentUrl(Integer orderId, HttpServletRequest request) {
        Order order = getOrderById(orderId);
        return getVnPayResponse(request, order);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public PaymentDto.VnPayResponse createVNPAYPaymentUrlForUser(Integer orderId, HttpServletRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = getOrderById(orderId);
        if (!Objects.equals(order.getCreatedBy().getId(), user.getId())) {
            throw new BadRequestException("Không tìm thấy đơn hàng mã: " + orderId);
        }
        return getVnPayResponse(request, order);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @Transactional
    public Order createOrderWithPromotion(UserCreateOrderWithPromotionRequest orderRequest, PaymentType paymentType) {
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
        order.setShippingFee(BigDecimal.valueOf(40000));
        order.setCountry("Việt Nam");

        // Calculate subtotal first
        BigDecimal subtotal = BigDecimal.ZERO;
        Set<OrderDetail> orderDetails = new LinkedHashSet<>();

        for (CartItem cartItem : cartItems) {
            if (cartItem.getStock().getQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Số lượng sản phẩm trong kho không đủ.");
            }
            if (cartItem.getQuantity() == 0) {
                throw new IllegalArgumentException("Số lượng sản phẩm không hợp lệ.");
            }
            if (cartItem.getQuantity() > 10) {
                throw new IllegalArgumentException("Bạn chỉ có thể mua 1 sản phẩm với số lượng tối đa 10. Hãy liên hệ với chúng tôi để có thể mua nhiều hơn.");
            }

            OrderDetail orderDetail = createOrderDetailFromCartItem(cartItem, order);
            orderDetails.add(orderDetail);

            // Update stock
            cartItem.getStock().setQuantity(cartItem.getStock().getQuantity() - cartItem.getQuantity());
            stockRepository.save(cartItem.getStock());

            // Calculate subtotal
            subtotal = subtotal.add(cartItem.getStock().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            cartItemRepository.delete(cartItem);
        }

        order.setOrderDetails(orderDetails);
        order.setSubtotal(subtotal);

        // Apply promotions
        applyPromotions(order, orderRequest.getProductPromotionCode(), orderRequest.getShippingPromotionCode());

        // Calculate final total
        calculateFinalTotal(order);

        return orderRepository.save(order);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Order createOrderWithPromotionForAdmin(AdminCreateOrderRequest orderRequest) {
        log.info("Bắt đầu tạo đơn hàng có mã giảm giá cho admin");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Order order = new Order();
        order.setCreatedBy(user);
        order.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        order.setStatus(orderRequest.getStatus());
        order.setPaymentType(orderRequest.getPaymentType());
        order.setFirstName(orderRequest.getCustomInfo().getFirstName());
        order.setLastName(orderRequest.getCustomInfo().getLastName());
        order.setEmail(orderRequest.getCustomInfo().getEmail());
        order.setPhone(orderRequest.getCustomInfo().getPhone());
        order.setAddress(orderRequest.getCustomInfo().getAddress());
        order.setWard(orderRequest.getCustomInfo().getWard());
        order.setDistrict(orderRequest.getCustomInfo().getDistrict());
        order.setProvince(orderRequest.getCustomInfo().getProvince());
        order.setCountry("Việt Nam");
        order.setShippingFee(orderRequest.getShippingFee());

        // Tạo order details và tính subtotal
        Set<OrderDetail> orderDetails = new LinkedHashSet<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (AdminCreateOrderRequest.OrderDetailRequest orderDetailRequest : orderRequest.getOrderDetails()) {
            Stock stock = stockRepository.findById(orderDetailRequest.getStockId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm với ID: " + orderDetailRequest.getStockId()));

            if (stock.getQuantity() < orderDetailRequest.getQuantity()) {
                throw new BadRequestException("Không đủ hàng cho sản phẩm ID: " + orderDetailRequest.getStockId());
            }

            // Giảm số lượng trong kho
            stock.setQuantity(stock.getQuantity() - orderDetailRequest.getQuantity());
            stockRepository.save(stock);

            // Tạo order detail
            OrderDetail orderDetail = createOrderDetailFromStock(stock, orderDetailRequest.getQuantity(), order);
            orderDetails.add(orderDetail);

            // Tính subtotal
            subtotal = subtotal.add(stock.getPrice().multiply(BigDecimal.valueOf(orderDetailRequest.getQuantity())));
        }

        order.setOrderDetails(orderDetails);
        order.setSubtotal(subtotal);

        // Áp dụng mã giảm giá
        applyPromotionsForAdmin(order, orderRequest.getProductPromotionCode(), orderRequest.getShippingPromotionCode());

        // Tính final total
        calculateFinalTotal(order);

        return orderRepository.save(order);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional(readOnly = true)
    public BigDecimal getOrderTotalRevenue(OrderStatus status, LocalDateTime fromDate, LocalDateTime toDate) {
        return orderRepository.getTotalRevenue(
                status == null ? OrderStatus.DELIVERED : status,
                fromDate == null ? LocalDateTime.of(1, 1, 1, 0, 0) : fromDate,
                toDate == null ? LocalDateTime.now() : toDate
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional(readOnly = true)
    public BigDecimal getAllOrderTotalRevenue(LocalDateTime fromDate, LocalDateTime toDate) {
        return orderRepository.getTotalRevenue(
                fromDate == null ? LocalDateTime.of(1, 1, 1, 0, 0) : fromDate,
                toDate == null ? LocalDateTime.now() : toDate
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional(readOnly = true)
    public Long getNumberOfOrders(OrderStatus status, LocalDateTime fromDate, LocalDateTime toDate) {
        return orderRepository.countOrdersByCreatedAtBetweenAndStatus(
                fromDate == null ? LocalDateTime.of(1, 1, 1, 0, 0) : fromDate,
                toDate == null ? LocalDateTime.now() : toDate,
                status == null ? OrderStatus.DELIVERED : status
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional(readOnly = true)
    public Long getAllNumberOfOrders(LocalDateTime fromDate, LocalDateTime toDate) {
        return orderRepository.countOrdersByCreatedAtBetween(
                fromDate == null ? LocalDateTime.of(1, 1, 1, 0, 0) : fromDate,
                toDate == null ? LocalDateTime.now() : toDate
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional(readOnly = true)
    public Long getNumberOfProductsSold(LocalDateTime fromDate, LocalDateTime toDate) {
        return orderRepository.countProductsSold(
                fromDate == null ? LocalDateTime.of(1, 1, 1, 0, 0) : fromDate,
                toDate == null ? LocalDateTime.now() : toDate
        );
    }

    private UserOrderDetailResponse mapToUserOrderDetailResponse(Order order) {
        UserOrderDetailResponse userOrderDetailResponse = new UserOrderDetailResponse();
        BeanUtils.copyProperties(order, userOrderDetailResponse);
        userOrderDetailResponse.setOrderDetails(order.getOrderDetails().stream()
                .map(this::convertOrderDetailToOrderDetailDto)
                .collect(Collectors.toSet()));
        if (order.getProductPromotion() != null) {
            userOrderDetailResponse.setProductProductPromotion(
                    new UserOrderDetailResponse.PromotionProductDto(
                            order.getProductPromotion().getId(),
                            order.getProductPromotion().getName(),
                            order.getProductPromotion().getCode()
                    )
            );
        }
        if (order.getShippingPromotion() != null) {
            userOrderDetailResponse.setShippingShippingPromotion(
                    new UserOrderDetailResponse.PromotionShippingDto(
                            order.getShippingPromotion().getId(),
                            order.getShippingPromotion().getName(),
                            order.getShippingPromotion().getCode()
                    )
            );
        }
        return userOrderDetailResponse;
    }

    private OrderDetail createOrderDetailFromStock(Stock stock, Integer quantity, Order order) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setStock(stock);
        orderDetail.setProduct(stock.getProduct());
        orderDetail.setProductName(stock.getProduct().getName());
        orderDetail.setQuantity(quantity);
        orderDetail.setPrice(stock.getPrice());
        orderDetail.setColorName(stock.getColor().getName());
        orderDetail.setVersionName(stock.getInstanceProperties().stream()
                .map(InstanceProperty::getName)
                .collect(Collectors.joining(", ")));
        orderDetail.setImageUrl(stock.getProductPhotos().stream()
                .findFirst()
                .map(ProductPhoto::getImageUrl)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hình ảnh sản phẩm.")));
        return orderDetail;
    }

    private void applyPromotionsForAdmin(Order order, String productPromotionCode, String shippingPromotionCode) {
        // Khởi tạo giá trị mặc định
        order.setProductDiscountAmount(BigDecimal.ZERO);
        order.setShippingDiscountAmount(BigDecimal.ZERO);

        // Áp dụng mã giảm giá sản phẩm
        if (productPromotionCode != null && !productPromotionCode.trim().isEmpty()) {
            Promotion productPromotion = promotionService.findValidPromotionByCode(
                    productPromotionCode);

            if (promotionService.isPromotionValid(productPromotion, order.getSubtotal())) {
                BigDecimal discountAmount = promotionService.calculateDiscountAmount(productPromotion, order.getSubtotal());
                order.setProductPromotion(productPromotion);
                order.setProductDiscountAmount(discountAmount);
                promotionService.incrementUsageCount(productPromotion);
                log.info("Áp dụng mã giảm giá sản phẩm: {} - Giảm: {}", productPromotionCode, discountAmount);
            } else {
                log.warn("Mã giảm giá sản phẩm không hợp lệ: {}", productPromotionCode);
                throw new BadRequestException("Mã giảm giá sản phẩm không hợp lệ hoặc đơn hàng không đủ điều kiện.");
            }
        }

        // Áp dụng mã giảm giá vận chuyển
        if (shippingPromotionCode != null && !shippingPromotionCode.trim().isEmpty()) {
            Promotion shippingPromotion = promotionService.findValidPromotionByCode(
                    shippingPromotionCode);

            if (promotionService.isPromotionValid(shippingPromotion, order.getSubtotal())) {
                BigDecimal discountAmount = promotionService.calculateDiscountAmount(shippingPromotion, order.getShippingFee());
                order.setShippingPromotion(shippingPromotion);
                order.setShippingDiscountAmount(discountAmount);
                promotionService.incrementUsageCount(shippingPromotion);
                log.info("Áp dụng mã giảm giá vận chuyển: {} - Giảm: {}", shippingPromotionCode, discountAmount);
            } else {
                log.warn("Mã giảm giá vận chuyển không hợp lệ: {}", shippingPromotionCode);
                throw new BadRequestException("Mã giảm giá vận chuyển không hợp lệ hoặc đơn hàng không đủ điều kiện.");
            }
        }
    }

    private OrderDetail createOrderDetailFromCartItem(CartItem cartItem, Order order) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(cartItem.getProduct());
        orderDetail.setProductName(cartItem.getProductName());
        orderDetail.setQuantity(cartItem.getQuantity());
        orderDetail.setPrice(cartItem.getStock().getPrice());
        orderDetail.setColorName(cartItem.getStock().getColor().getName());
        orderDetail.setVersionName(cartItem.getStock().getInstanceProperties().stream()
                .map(InstanceProperty::getName)
                .collect(Collectors.joining(", ")));
        orderDetail.setImageUrl(cartItem.getStock().getProductPhotos().stream()
                .findFirst()
                .map(ProductPhoto::getImageUrl)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hình ảnh sản phẩm.")));
        orderDetail.setStock(cartItem.getStock());
        return orderDetail;
    }

    private void applyPromotions(Order order, String productPromotionCode, String shippingPromotionCode) {
        // Apply product promotion
        if (productPromotionCode != null && !productPromotionCode.trim().isEmpty()) {
            Promotion productPromotion = promotionService.findValidPromotionByCode(
                    productPromotionCode);

            if (promotionService.isPromotionValid(productPromotion, order.getSubtotal())) {
                BigDecimal discountAmount = promotionService.calculateDiscountAmount(productPromotion, order.getSubtotal());
                order.setProductPromotion(productPromotion);
                order.setProductDiscountAmount(discountAmount);
                promotionService.incrementUsageCount(productPromotion);
            } else {
                throw new BadRequestException("Mã giảm giá sản phẩm không hợp lệ hoặc đơn hàng không đủ điều kiện.");
            }
        } else {
            order.setProductPromotion(null);
            order.setProductDiscountAmount(BigDecimal.ZERO);
        }

        // Apply shipping promotion
        if (shippingPromotionCode != null && !shippingPromotionCode.trim().isEmpty()) {
            Promotion shippingPromotion = promotionService.findValidPromotionByCode(
                    shippingPromotionCode);

            if (promotionService.isPromotionValid(shippingPromotion, order.getSubtotal())) {
                BigDecimal discountAmount = promotionService.calculateDiscountAmount(shippingPromotion, order.getShippingFee());
                log.info("Applying shipping promotion: {} - Giảm: {}", shippingPromotionCode, discountAmount);
                order.setShippingPromotion(shippingPromotion);
                order.setShippingDiscountAmount(discountAmount);
                promotionService.incrementUsageCount(shippingPromotion);
            } else {
                throw new BadRequestException("Mã giảm giá vận chuyển không hợp lệ hoặc đơn hàng không đủ điều kiện.");
            }
        } else {
            order.setShippingPromotion(null);
            order.setShippingDiscountAmount(BigDecimal.ZERO);
        }
    }

    private void calculateFinalTotal(Order order) {
        BigDecimal finalTotal = order.getSubtotal()
                .subtract(order.getProductDiscountAmount())
                .add(order.getShippingFee())
                .subtract(order.getShippingDiscountAmount());

        order.setFinalTotal(finalTotal.add(finalTotal.multiply(BigDecimal.valueOf(0.1))));
    }

    private PaymentDto.VnPayResponse getVnPayResponse(HttpServletRequest request, Order order) {
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
                order.getFinalTotal(),
                order.getOrderDetails().stream().map(this::convertOrderDetailToOrderDetailDto).collect(Collectors.toSet()),
                order.getShippingTrackingCode()
        );
    }

    private OrderUserResponse.OrderDetailDto convertOrderDetailToOrderDetailDto(OrderDetail orderDetail) {
        return new OrderUserResponse.OrderDetailDto(
                orderDetail.getId(),
                new OrderUserResponse.OrderDetailDto.ProductDto(orderDetail.getProduct() == null ? null : orderDetail.getProduct().getId()),
                orderDetail.getProductName(),
                orderDetail.getQuantity(),
                orderDetail.getPrice(),
                orderDetail.getNote(),
                orderDetail.getColorName(),
                orderDetail.getVersionName(),
                orderDetail.getImageUrl()
        );
    }

    private OrderSummaryV2Dto convertOrderToOrderSummaryV2Dto(Order order) {
        return new OrderSummaryV2Dto(
                order.getId(),
                order.getCreatedAt(),
                order.getPaymentType(),
                order.getApproveAt(),
                order.getStatus(),
                new OrderSummaryV2Dto.UserSummary(
                        order.getCreatedBy().getId(),
                        order.getCreatedBy().getFirstName(),
                        order.getCreatedBy().getLastName(),
                        order.getCreatedBy().getImage()
                )
        );
    }
}
