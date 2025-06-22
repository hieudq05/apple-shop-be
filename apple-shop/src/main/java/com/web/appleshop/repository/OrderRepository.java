package com.web.appleshop.repository;

import com.web.appleshop.entity.Order;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Integer> , JpaSpecificationExecutor<Order> {
  @EntityGraph(attributePaths = {"orderDetails", "orderDetails.stock"})
    Optional<Order> findByIdWithDetails(Integer id);
    
  }