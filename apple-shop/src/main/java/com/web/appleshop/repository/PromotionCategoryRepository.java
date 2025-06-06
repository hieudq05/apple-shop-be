package com.web.appleshop.repository;

import com.web.appleshop.entity.PromotionCategory;
import com.web.appleshop.entity.PromotionCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PromotionCategoryRepository extends JpaRepository<PromotionCategory, PromotionCategoryId> , JpaSpecificationExecutor<PromotionCategory> {
  }