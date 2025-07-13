package com.web.appleshop.repository;

import com.web.appleshop.dto.projection.OrderSummaryProjection;
import com.web.appleshop.dto.response.admin.OrderSummaryDto;
import com.web.appleshop.entity.Order;
import com.web.appleshop.entity.User;
import com.web.appleshop.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

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
}