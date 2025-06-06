package com.web.appleshop.repository;

import com.web.appleshop.entity.PromotionProduct;
import com.web.appleshop.entity.PromotionProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PromotionProductRepository extends JpaRepository<PromotionProduct, PromotionProductId> , JpaSpecificationExecutor<PromotionProduct> {
  }