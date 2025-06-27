package com.web.appleshop.service.impl;

import com.web.appleshop.dto.projection.FeatureInfoView;
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
    public Page<FeatureInfoView> getFeaturesForAdmin(Pageable pageable) {
        return featureRepository.findAllBy(pageable);
    }
}
