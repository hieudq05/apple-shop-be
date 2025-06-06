package com.web.appleshop.repository;

import com.web.appleshop.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> , JpaSpecificationExecutor<Promotion> {
  }