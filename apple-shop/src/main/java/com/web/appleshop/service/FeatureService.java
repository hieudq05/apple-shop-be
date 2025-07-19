package com.web.appleshop.service;

import com.web.appleshop.dto.projection.FeatureInfoView;
import com.web.appleshop.dto.request.AdminFeatureRequest;
import com.web.appleshop.entity.Feature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface FeatureService {
    Page<FeatureInfoView> getFeaturesForAdmin(Pageable pageable);

    Feature createFeature(AdminFeatureRequest request, MultipartFile file);

    Feature updateFeature(Integer featureId, AdminFeatureRequest request, MultipartFile file);

    void deleteFeature(Integer featureId);
}
