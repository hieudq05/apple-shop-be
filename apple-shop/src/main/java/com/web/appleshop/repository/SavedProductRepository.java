package com.web.appleshop.repository;

import com.web.appleshop.entity.SavedProduct;
import com.web.appleshop.entity.SavedProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SavedProductRepository extends JpaRepository<SavedProduct, SavedProductId> , JpaSpecificationExecutor<SavedProduct> {
  }