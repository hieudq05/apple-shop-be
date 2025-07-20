package com.web.appleshop.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for Product search criteria with flexible filtering options
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class AdminProductSearchCriteria extends BaseProductSearchCriteria {
    // Stock quantity filtering
    private Integer minQuantity;
    private Integer maxQuantity;

    // Date range filtering
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private LocalDateTime updatedAfter;
    private LocalDateTime updatedBefore;

    // Creator filtering
    private Integer createdById;
    private String createdByEmail;

    // Status filtering
    private Boolean isDeleted;

    // Advanced filters for future expansion
    private Set<Integer> promotionIds;

    @Override
    public boolean hasJoins() {
        return getCategoryId() != null || getCategoryName() != null ||
                (getFeatureIds() != null && !getFeatureIds().isEmpty()) ||
                (getFeatureNames() != null && !getFeatureNames().isEmpty()) ||
                (getColorIds() != null && !getColorIds().isEmpty()) ||
                (getColorNames() != null && !getColorNames().isEmpty()) ||
                getCreatedById() != null || getCreatedByEmail() != null ||
                (getPromotionIds() != null && !getPromotionIds().isEmpty());
    }

    @Override
    public Set<String> getAllowedSortFields() {
        return Set.of(
                "id", "name", "description", "price", "quantity",
                "createdAt", "updatedAt", "categoryName",
                "createdByName", "isDeleted", "rating"
        );
    }

    @Override
    public boolean isEmpty() {
        boolean baseEmpty = super.isEmpty();
        return baseEmpty &&
                minQuantity == null && maxQuantity == null &&
                createdAfter == null && createdBefore == null &&
                updatedAfter == null && updatedBefore == null &&
                createdById == null && createdByEmail == null &&
                isDeleted == null &&
                (promotionIds == null || promotionIds.isEmpty());
    }
}
