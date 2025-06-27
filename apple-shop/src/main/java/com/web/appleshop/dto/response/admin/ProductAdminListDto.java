package com.web.appleshop.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.Product}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAdminListDto implements Serializable {
    Integer id;
    String name;
    String description;
    LocalDateTime createdAt;
    String createdBy;
    Integer categoryId;
    String categoryName;

    Set<FeatureSummaryDto> features;
    Set<StockSummaryDto> stocks;

    public ProductAdminListDto(Integer id, String name, String description, LocalDateTime createdAt, String createdBy, Integer categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.features = new LinkedHashSet<>();
        this.stocks = new LinkedHashSet<>();
    }
}