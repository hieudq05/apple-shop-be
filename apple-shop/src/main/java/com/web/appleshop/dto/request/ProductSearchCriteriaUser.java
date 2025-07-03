package com.web.appleshop.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class ProductSearchCriteriaUser extends ProductSearchCriteria{
    @Override
    public boolean hasJoins() {
        return getCategoryId() != null || getCategoryName() != null ||
                (getFeatureIds() != null && !getFeatureIds().isEmpty()) ||
                (getFeatureNames() != null && !getFeatureNames().isEmpty()) ||
                (getColorIds() != null && !getColorIds().isEmpty()) ||
                (getColorNames() != null && !getColorNames().isEmpty());
    }

    @Override
    public Set<String> getAllowedSortFields() {
        return Set.of(
                "name",
                "price",
                "createdAt",
                "categoryName",
                "rating" // if rating is public
        );
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }
}
