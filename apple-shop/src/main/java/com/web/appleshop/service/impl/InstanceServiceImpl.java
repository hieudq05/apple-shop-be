package com.web.appleshop.service.impl;

import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.entity.InstanceProperty;
import com.web.appleshop.repository.InstancePropertyRepository;
import com.web.appleshop.service.InstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstanceServiceImpl implements InstanceService {

    private final InstancePropertyRepository instancePropertyRepository;

    @Override
    public Page<ProductAdminResponse.ProductStockAdminResponse.InstancePropertyDto> getInstancesForAdmin(Pageable pageable) {
        return instancePropertyRepository.findAll(pageable).map(InstanceServiceImpl::convertInstanceToInstanceAdmin);
    }

    public static ProductAdminResponse.ProductStockAdminResponse.InstancePropertyDto convertInstanceToInstanceAdmin(InstanceProperty instanceProperty) {
        return new ProductAdminResponse.ProductStockAdminResponse.InstancePropertyDto(
                instanceProperty.getId(),
                instanceProperty.getName()
        );
    }
}
