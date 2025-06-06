package com.web.appleshop.repository;

import com.web.appleshop.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StockRepository extends JpaRepository<Stock, Integer> , JpaSpecificationExecutor<Stock> {
  }