package com.web.appleshop.dto.projection;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

/**
 * Projection for {@link com.web.appleshop.entity.Feature}
 */
public interface FeatureInfoView {
    Integer getId();

    String getName();

    String getDescription();

    String getImage();

    LocalDateTime getCreatedAt();

    @Value("#{target.createdBy.firstName} #{target.createdBy.lastName}")
    String getCreatedByName();

    @Value("#{target.createdBy.image}")
    String getCreatedByImage();
}