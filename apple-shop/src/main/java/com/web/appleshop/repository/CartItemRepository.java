package com.web.appleshop.repository;

import com.web.appleshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> , JpaSpecificationExecutor<CartItem> {
  }