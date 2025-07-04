package com.web.appleshop.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public abstract class BaseProductSearchCriteria {
    // Basic product information
    private String name;
    private String description;

    // Category filtering
    private Set<Integer> categoryId;
    private Set<String> categoryName;

    // Feature filtering
    private Set<Integer> featureIds;
    private Set<String> featureNames;

    // Color filtering
    private Set<Integer> colorIds;
    private Set<String> colorNames;

    // Price range filtering
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    // Instance properties filtering
    private Set<Integer> instancePropertyIds;
    private Set<String> instancePropertyNames;

    // Full-text search
    private String searchKeyword; // Will search across name, description

    // Advanced filters for future expansion
    private Boolean hasReviews;
    private Double minRating;
    private Double maxRating;
    private Boolean inStock; // Products with quantity > 0

    // Sorting options
    private String sortBy; // name, price, createdAt, updatedAt, etc.
    private String sortDirection; // ASC, DESC

    /**
     * Abstract method to determine if this criteria has joins
     */
    public abstract boolean hasJoins();

    /**
     * Abstract method to get allowed sort fields for each role
     */
    public abstract Set<String> getAllowedSortFields();

    /**
     * Check if the criteria is empty (no filters applied)
     */
    public boolean isEmpty() {
        return name == null && description == null && searchKeyword == null &&
                categoryId == null && categoryName == null &&
                (featureIds == null || featureIds.isEmpty()) &&
                (featureNames == null || featureNames.isEmpty()) &&
                (colorIds == null || colorIds.isEmpty()) &&
                (colorNames == null || colorNames.isEmpty()) &&
                minPrice == null && maxPrice == null &&
                inStock == null && hasReviews == null &&
                minRating == null && maxRating == null;
    }
}
