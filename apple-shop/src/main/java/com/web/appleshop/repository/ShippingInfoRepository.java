package com.web.appleshop.repository;

import com.web.appleshop.entity.ShippingInfo;
import com.web.appleshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShippingInfoRepository extends JpaRepository<ShippingInfo, Integer>, JpaSpecificationExecutor<ShippingInfo> {
    Page<ShippingInfo> findShippingInfosByUser(User user, Pageable pageable);

    ShippingInfo findShippingInfoByUserAndIsDefault(User user, Boolean isDefault);

    ShippingInfo findShippingInfoByUserAndWardAndDistrictAndProvinceAndAddress(User user, String ward, String district, String province, String address);
}