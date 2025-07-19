package com.web.appleshop.dto.projection;

/**
 * Projection for {@link com.web.appleshop.entity.Category}
 */
public interface CategoryInfoView {
    Integer getId();

    String getName();

    String getDescription();

    String getImage();
}