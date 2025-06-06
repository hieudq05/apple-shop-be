package com.web.appleshop.repository;

import com.web.appleshop.entity.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Integer> , JpaSpecificationExecutor<ProductPhoto> {
  }