package com.web.appleshop.repository;

import com.web.appleshop.entity.ShippingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShippingInfoRepository extends JpaRepository<ShippingInfo, Integer> , JpaSpecificationExecutor<ShippingInfo> {
  }