package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.AdminInstancePropertyRequest;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.entity.InstanceProperty;
import com.web.appleshop.entity.User;
import com.web.appleshop.repository.InstancePropertyRepository;
import com.web.appleshop.service.InstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class InstanceServiceImpl implements InstanceService {

    private final InstancePropertyRepository instancePropertyRepository;

    @Override
    public Page<ProductAdminResponse.ProductStockAdminResponse.InstancePropertyDto> getInstancesForAdmin(Pageable pageable) {
        return instancePropertyRepository.findAll(pageable).map(InstanceServiceImpl::convertInstanceToInstanceAdmin);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public void deleteInstance(Integer instanceId) {
        instancePropertyRepository.deleteById(instanceId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public InstanceProperty createInstance(AdminInstancePropertyRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        InstanceProperty instanceProperty = new InstanceProperty();
        instanceProperty.setName(request.getName());
        instanceProperty.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        instanceProperty.setCreatedBy(user);
        return instancePropertyRepository.save(instanceProperty);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public InstanceProperty updateInstance(Integer instanceId, AdminInstancePropertyRequest request) {
        InstanceProperty instanceProperty = instancePropertyRepository.findById(instanceId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy thuộc tính.")
        );
        instanceProperty.setName(request.getName());
        return instancePropertyRepository.save(
                instanceProperty
        );
    }

    public static ProductAdminResponse.ProductStockAdminResponse.InstancePropertyDto convertInstanceToInstanceAdmin(InstanceProperty instanceProperty) {
        return new ProductAdminResponse.ProductStockAdminResponse.InstancePropertyDto(
                instanceProperty.getId(),
                instanceProperty.getName()
        );
    }
}
