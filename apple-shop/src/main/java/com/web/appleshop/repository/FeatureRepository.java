package com.web.appleshop.repository;

import com.web.appleshop.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeatureRepository extends JpaRepository<Feature, Integer> , JpaSpecificationExecutor<Feature> {
  }