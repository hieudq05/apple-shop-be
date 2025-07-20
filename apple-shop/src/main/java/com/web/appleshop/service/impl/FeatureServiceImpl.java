package com.web.appleshop.service.impl;

import com.web.appleshop.dto.projection.FeatureInfoView;
import com.web.appleshop.dto.request.AdminFeatureRequest;
import com.web.appleshop.entity.Feature;
import com.web.appleshop.entity.User;
import com.web.appleshop.repository.FeatureRepository;
import com.web.appleshop.service.FeatureService;
import com.web.appleshop.util.UploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class FeatureServiceImpl implements FeatureService {

    private final FeatureRepository featureRepository;
    private final UploadUtils uploadUtils;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Page<FeatureInfoView> getFeaturesForAdmin(Pageable pageable) {
        return featureRepository.findAllBy(pageable);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Feature createFeature(AdminFeatureRequest request, MultipartFile file) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Feature feature = new Feature();
        if (file != null && !file.isEmpty()) {
            feature.setImage(uploadUtils.uploadFile(file));
        } else {
            feature.setImage(request.getImage());
        }
        feature.setName(request.getName());
        feature.setDescription(request.getDescription());
        feature.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        feature.setCreatedBy(user);
        return featureRepository.save(feature);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Feature updateFeature(Integer featureId, AdminFeatureRequest request, MultipartFile file) {
        Feature feature = featureRepository.findById(featureId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy thuộc tính.")
        );
        if (file != null && !file.isEmpty()) {
            feature.setImage(uploadUtils.uploadFile(file));
        } else {
            feature.setImage(request.getImage());
        }
        feature.setName(request.getName());
        feature.setDescription(request.getDescription());
        return featureRepository.save(
                feature
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public void deleteFeature(Integer featureId) {
        featureRepository.deleteById(featureId);
    }
}
