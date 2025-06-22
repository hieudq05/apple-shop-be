package com.web.appleshop.repository;

import com.web.appleshop.entity.OrderStatus;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> , JpaSpecificationExecutor<OrderStatus> {
  Optional<OrderStatus> findByName(String name);
  }