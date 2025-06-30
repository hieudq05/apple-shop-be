package com.web.appleshop.service;

import com.web.appleshop.dto.projection.FeatureInfoView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeatureService {
    Page<FeatureInfoView> getFeaturesForAdmin(Pageable pageable);
}
