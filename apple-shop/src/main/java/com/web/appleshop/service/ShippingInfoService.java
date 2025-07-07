package com.web.appleshop.service;

import com.web.appleshop.dto.request.UserShippingInfoRequest;
import com.web.appleshop.dto.response.UserShippingInfoDto;
import com.web.appleshop.entity.ShippingInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShippingInfoService {
    ShippingInfo createShippingInfo(UserShippingInfoRequest request);

    ShippingInfo updateShippingInfo(Integer shippingInfoId, UserShippingInfoRequest request);

    void deleteShippingInfo(Integer shippingInfoId);

    Page<UserShippingInfoDto> getMyShippingInfo(Pageable pageable);

    ShippingInfo setDefaultShippingInfo(Integer shippingInfoId);
}
