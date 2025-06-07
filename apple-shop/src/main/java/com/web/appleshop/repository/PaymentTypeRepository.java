package com.web.appleshop.repository;

import com.web.appleshop.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Integer> , JpaSpecificationExecutor<PaymentType> {
  }