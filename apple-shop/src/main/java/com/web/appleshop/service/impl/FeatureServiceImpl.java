package com.web.appleshop.service.impl;

import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.entity.Feature;
import com.web.appleshop.repository.FeatureRepository;
import com.web.appleshop.service.FeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeatureServiceImpl implements FeatureService {

    private final FeatureRepository featureRepository;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Page<ProductAdminResponse.FeatureAdminResponse> getFeaturesForAdmin(Pageable pageable) {
        return featureRepository.findAll(pageable).map(FeatureServiceImpl::convertFeatureToFeatureAdmin);
    }

    public static ProductAdminResponse.FeatureAdminResponse convertFeatureToFeatureAdmin(Feature feature) {
        return new ProductAdminResponse.FeatureAdminResponse(
                feature.getId(),
                feature.getName(),
                feature.getDescription(),
                feature.getImage()
        );
    }
}
