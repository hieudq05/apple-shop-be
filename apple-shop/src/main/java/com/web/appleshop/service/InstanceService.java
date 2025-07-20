package com.web.appleshop.service;

import com.web.appleshop.dto.request.AdminInstancePropertyRequest;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.entity.InstanceProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstanceService {
    Page<ProductAdminResponse.ProductStockAdminResponse.InstancePropertyDto> getInstancesForAdmin(Pageable pageable);

    void deleteInstance(Integer instanceId);

    InstanceProperty createInstance(AdminInstancePropertyRequest request);

    InstanceProperty updateInstance(Integer instanceId, AdminInstancePropertyRequest request);
}
