package com.web.appleshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for Product search criteria with flexible filtering options
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCriteria {
    
    // Basic product information
    private String name;
    private String description;
    
    // Category filtering
    private Integer categoryId;
    private String categoryName;
    
    // Feature filtering
    private Set<Integer> featureIds;
    private Set<String> featureNames;
    
    // Color filtering
    private Set<Integer> colorIds;
    private Set<String> colorNames;
    
    // Price range filtering
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    
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
    
    // Instance properties filtering
    private Set<Integer> instancePropertyIds;
    private Set<String> instancePropertyNames;
    
    // Full-text search
    private String searchKeyword; // Will search across name, description
    
    // Advanced filters for future expansion
    private Set<Integer> promotionIds;
    private Boolean hasReviews;
    private Double minRating;
    private Double maxRating;
    private Boolean inStock; // Products with quantity > 0
    
    // Sorting options
    private String sortBy; // name, price, createdAt, updatedAt, etc.
    private String sortDirection; // ASC, DESC
}
