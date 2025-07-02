package com.web.appleshop.dto.response.admin;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.web.appleshop.entity.Feature}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeatureSummaryDto implements Serializable {
    Integer id;
    String name;
    String image;
}