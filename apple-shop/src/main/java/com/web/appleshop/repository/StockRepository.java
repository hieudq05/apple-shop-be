package com.web.appleshop.repository;

import com.web.appleshop.entity.Product;
import com.web.appleshop.entity.Stock;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, Integer> , JpaSpecificationExecutor<Stock> {
   Optional<Stock> findByProductAndColor(Product product, String colorName);
    
    @Query("SELECT s FROM Stock s JOIN s.color c WHERE s.product = :product AND c.name = :colorName")
    Optional<Stock> findByProductAndColorName(
        @Param("product") Product product, 
        @Param("colorName") String colorName
    );
  }