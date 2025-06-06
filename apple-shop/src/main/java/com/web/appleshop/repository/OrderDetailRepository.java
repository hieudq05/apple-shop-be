package com.web.appleshop.repository;

import com.web.appleshop.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> , JpaSpecificationExecutor<OrderDetail> {
  }