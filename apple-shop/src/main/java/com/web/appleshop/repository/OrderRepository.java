package com.web.appleshop.repository;

import com.web.appleshop.entity.Order;
import com.web.appleshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> , JpaSpecificationExecutor<Order> {
    Optional<Order> findOrderById(Integer id);

    Page<Order> findOrdersByCreatedBy(User createdBy, Pageable pageable);
}