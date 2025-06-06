package com.web.appleshop.repository;

import com.web.appleshop.entity.PromotionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PromotionTypeRepository extends JpaRepository<PromotionType, Integer> , JpaSpecificationExecutor<PromotionType> {
  }