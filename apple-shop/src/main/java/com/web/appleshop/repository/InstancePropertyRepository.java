package com.web.appleshop.repository;

import com.web.appleshop.entity.InstanceProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InstancePropertyRepository extends JpaRepository<InstanceProperty, Integer> , JpaSpecificationExecutor<InstanceProperty> {
  }