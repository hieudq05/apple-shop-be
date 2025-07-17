package com.web.appleshop.repository;

import com.web.appleshop.dto.projection.OrderSummaryProjection;
import com.web.appleshop.entity.Order;
import com.web.appleshop.entity.User;
import com.web.appleshop.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    Optional<Order> findOrderById(Integer id);

    Page<Order> findOrdersByCreatedBy(User createdBy, Pageable pageable);

    Page<OrderSummaryProjection> findAllBy(Pageable pageable);

    @Query("""
            select (count(o) > 0) from Order o inner join o.orderDetails orderDetails
            where o.id = ?1 and orderDetails.stock.id = ?2 and o.status = ?3""")
    boolean existsByUserIdAndStockIdAndStatus(Integer id, Integer id1, OrderStatus status);

    Optional<Order> findOrderByIdAndCreatedBy(Integer id, User createdBy);

    /**
     * Statistics
     */

    // Get total revenue from order
    @Query("""
                select sum(o.finalTotal)
                from Order o
                where o.status = :status and o.createdAt between :fromDate and :toDate
            """)
    BigDecimal getTotalRevenue(@Param("status") OrderStatus status, @Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    @Query("""
                select sum(o.finalTotal)
                from Order o
                where o.createdAt between :fromDate and :toDate
            """)
    BigDecimal getTotalRevenue(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    Long countOrdersByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    Long countOrdersByCreatedAtBetweenAndStatus(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore, OrderStatus status);

    @Query("""
                select sum(od.quantity)
                from Order o
                            join OrderDetail od on od.order.id = o.id
                where o.status = 'DELIVERED' and o.createdAt between :fromDate and :toDate
            """)
    Long countProductsSold(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}