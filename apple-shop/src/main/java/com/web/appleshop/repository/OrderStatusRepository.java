package com.web.appleshop.repository;

import com.web.appleshop.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> , JpaSpecificationExecutor<OrderStatus> {
  }