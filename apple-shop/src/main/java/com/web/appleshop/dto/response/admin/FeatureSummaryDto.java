package com.web.appleshop.dto.response.admin;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.web.appleshop.entity.Feature}
 */
@Value
public class FeatureSummaryDto implements Serializable {
    Integer id;
    String name;
    String image;
}