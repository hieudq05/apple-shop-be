package com.web.appleshop.repository;

import com.web.appleshop.entity.ProductFeature;
import com.web.appleshop.entity.ProductFeatureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductFeatureRepository extends JpaRepository<ProductFeature, ProductFeatureId> , JpaSpecificationExecutor<ProductFeature> {
  }