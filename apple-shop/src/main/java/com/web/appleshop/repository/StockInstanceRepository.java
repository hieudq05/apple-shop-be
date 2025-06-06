package com.web.appleshop.repository;

import com.web.appleshop.entity.StockInstance;
import com.web.appleshop.entity.StockInstanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StockInstanceRepository extends JpaRepository<StockInstance, StockInstanceId> , JpaSpecificationExecutor<StockInstance> {
  }