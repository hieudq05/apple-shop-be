package com.web.appleshop.specification;

import com.web.appleshop.dto.request.ProductSearchCriteria;
import com.web.appleshop.dto.request.ProductSearchCriteriaAdmin;
import com.web.appleshop.entity.*;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Product specifications for dynamic query building
 * This class provides a flexible and extensible way to build database queries
 * based on various search criteria.
 */
public class ProductSpecification {

    private static final Logger log = LoggerFactory.getLogger(ProductSpecification.class);

    /**
     * Main method to create specification based on search criteria
     */
    public static <T extends ProductSearchCriteria> Specification<Product> createSpecification(T criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Objects.requireNonNull(query, "Query must not be null");
            addFetchJoins(root, query, criteria);

            // Basic product information filters
            addNameFilter(criteria.getName(), root, criteriaBuilder, predicates);
            addDescriptionFilter(criteria.getDescription(), root, criteriaBuilder, predicates);
            addKeywordSearchFilter(criteria.getSearchKeyword(), root, criteriaBuilder, predicates);

            // Category filters
            addCategoryIdFilter(criteria.getCategoryId(), root, criteriaBuilder, predicates);
            addCategoryNameFilter(criteria.getCategoryName(), root, criteriaBuilder, predicates);

            // Feature filters
            addFeatureFilters(criteria.getFeatureIds(), criteria.getFeatureNames(), root, criteriaBuilder, predicates);

            // Color filters (through stocks)
            addColorFilters(criteria.getColorIds(), criteria.getColorNames(), root, criteriaBuilder, predicates);

            // Price range filters (through stocks)
            addPriceRangeFilter(criteria.getMinPrice(), criteria.getMaxPrice(), root, criteriaBuilder, predicates);

            // Instance property filters
            addInstancePropertyFilters(criteria.getInstancePropertyIds(), criteria.getInstancePropertyNames(), root, criteriaBuilder, predicates);

            // Stock availability filter
            addStockAvailabilityFilter(criteria.getInStock(), root, criteriaBuilder, predicates);

            // Review property filters
            addReviewFilters(criteria.getHasReviews(), criteria.getMinRating(), criteria.getMaxRating(), root, criteriaBuilder, predicates);

            if (criteria instanceof ProductSearchCriteriaAdmin adminCriteria) {
                log.info("Admin criteria detected");

                // Stock quantity filters
                addQuantityRangeFilter(adminCriteria.getMinQuantity(), adminCriteria.getMaxQuantity(), root, criteriaBuilder, predicates);

                // Date range filters
                addDateRangeFilters(adminCriteria, root, criteriaBuilder, predicates);

                // Creator filters
                addCreatorFilters(adminCriteria.getCreatedById(), adminCriteria.getCreatedByEmail(), root, criteriaBuilder, predicates);

                // Status filters
                addStatusFilter(adminCriteria.getIsDeleted(), root, criteriaBuilder, predicates);

                // Promotion filters
                addPromotionFilter(adminCriteria.getPromotionIds(), root, criteriaBuilder, predicates);
            }

            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static <T extends ProductSearchCriteria> void addFetchJoins(Root<Product> root, CriteriaQuery<?> query, T criteria) {
        // Only add fetch joins for non-count queries
        if (Long.class != query.getResultType()) {
            try {
                // Fetch category (most commonly accessed)
                root.fetch("category", JoinType.LEFT);

                if (criteria instanceof ProductSearchCriteriaAdmin adminCriteria) {
                    // Fetch creator information with roles
                    Fetch<Product, User> createdByFetch = root.fetch("createdBy", JoinType.LEFT);
                    createdByFetch.fetch("roles", JoinType.LEFT);
                }

                // Fetch stocks with complete information
                Fetch<Product, Stock> stocksFetch = root.fetch("stocks", JoinType.LEFT);
                stocksFetch.fetch("color", JoinType.LEFT);

                // Fetch instance properties through stocks
                stocksFetch.fetch("instanceProperties", JoinType.LEFT);

                // Fetch product photos through stocks (assuming photos are linked to stocks)
                stocksFetch.fetch("productPhotos", JoinType.LEFT);

                // Fetch features
                root.fetch("features", JoinType.LEFT);
                root.fetch("product_features", JoinType.LEFT);

                // Note: Be careful with promotions and instanceProperties as they might create cartesian products
                // Only fetch them if specifically needed

            } catch (Exception e) {
                // In case of any fetch join issues, continue without them
                // This prevents the entire query from failing
            }
        }
    }

    /**
     * Add review/rating filters (public filters)
     */
    private static void addReviewFilters(Boolean hasReviews, Double minRating, Double maxRating,
                                         Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (hasReviews != null) {
            if (hasReviews) {
                // Products that have reviews
                predicates.add(cb.isNotEmpty(root.get("reviews")));
            } else {
                // Products that don't have reviews
                predicates.add(cb.isEmpty(root.get("reviews")));
            }
        }

        if (minRating != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), minRating));
        }

        if (maxRating != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("rating"), maxRating));
        }
    }

    /**
     * Add name filter (case-insensitive partial match)
     */
    private static void addNameFilter(String name, Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(name)) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
    }

    /**
     * Add description filter (case-insensitive partial match)
     */
    private static void addDescriptionFilter(String description, Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(description)) {
            predicates.add(cb.like(root.get("description"), "%" + description.toLowerCase() + "%"));
        }
    }

    /**
     * Add keyword search across multiple fields
     */
    private static void addKeywordSearchFilter(String keyword, Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(keyword)) {
            String lowerSearchPattern = "%" + keyword.toLowerCase() + "%";
            String upperSearchPattern = "%" + keyword.toUpperCase() + "%";

            Predicate nameMatch = cb.like(cb.lower(root.get("name")), lowerSearchPattern);
            Predicate descriptionMatch = cb.like(root.get("description"), upperSearchPattern);
            predicates.add(cb.or(nameMatch, descriptionMatch));
        }
    }

    /**
     * Add category ID filter
     */
    private static void addCategoryIdFilter(Integer categoryId, Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (categoryId != null) {
            predicates.add(cb.equal(root.get("category").get("id"), categoryId));
        }
    }

    /**
     * Add category name filter (case-insensitive partial match)
     */
    private static void addCategoryNameFilter(String categoryName, Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(categoryName)) {
            predicates.add(cb.like(cb.lower(root.get("category").get("name")), "%" + categoryName.toLowerCase() + "%"));
        }
    }

    /**
     * Add feature filters
     */
    private static void addFeatureFilters(Set<Integer> featureIds, Set<String> featureNames,
                                          Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        Join<Product, Feature> featureJoin = root.join("features", JoinType.INNER);
        if (featureIds != null && !featureIds.isEmpty()) {
            predicates.add(featureJoin.get("id").in(featureIds));
        }
        if (featureNames != null && !featureNames.isEmpty()) {
            List<Predicate> featureNamePredicates = new ArrayList<>();
            for (String featureName : featureNames) {
                featureNamePredicates.add(cb.like(cb.lower(featureJoin.get("name")), "%" + featureName.toLowerCase() + "%"));
            }
            predicates.add(cb.or(featureNamePredicates.toArray(new Predicate[0])));
        }
    }

    /**
     * Add color filters through stocks
     */
    private static void addColorFilters(Set<Integer> colorIds, Set<String> colorNames,
                                        Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (colorIds != null && !colorIds.isEmpty()) {
            Join<Product, Stock> stockJoin = root.join("stocks", JoinType.INNER);
            predicates.add(stockJoin.get("color").get("id").in(colorIds));
        }

        if (colorNames != null && !colorNames.isEmpty()) {
            Join<Product, Stock> stockJoin = root.join("stocks", JoinType.INNER);
            Join<Stock, Color> colorJoin = stockJoin.join("color", JoinType.INNER);
            List<Predicate> colorNamePredicates = new ArrayList<>();
            for (String colorName : colorNames) {
                colorNamePredicates.add(cb.like(cb.lower(colorJoin.get("name")), "%" + colorName.toLowerCase() + "%"));
            }
            predicates.add(cb.or(colorNamePredicates.toArray(new Predicate[0])));
        }
    }

    /**
     * Add price range filter
     */
    private static void addPriceRangeFilter(BigDecimal minPrice, BigDecimal maxPrice,
                                            Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (minPrice != null || maxPrice != null) {
            Join<Product, Stock> stockJoin = root.join("stocks", JoinType.INNER);

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(stockJoin.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(stockJoin.get("price"), maxPrice));
            }
        }
    }

    /**
     * Add quantity range filter
     */
    private static void addQuantityRangeFilter(Integer minQuantity, Integer maxQuantity,
                                               Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (minQuantity != null || maxQuantity != null) {
            Join<Product, Stock> stockJoin = root.join("stocks", JoinType.INNER);

            if (minQuantity != null) {
                predicates.add(cb.greaterThanOrEqualTo(stockJoin.get("quantity"), minQuantity));
            }

            if (maxQuantity != null) {
                predicates.add(cb.lessThanOrEqualTo(stockJoin.get("quantity"), maxQuantity));
            }
        }
    }

    /**
     * Add date range filters
     */
    private static void addDateRangeFilters(ProductSearchCriteriaAdmin criteria, Root<Product> root,
                                            CriteriaBuilder cb, List<Predicate> predicates) {
        if (criteria.getCreatedAfter() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAfter()));
        }

        if (criteria.getCreatedBefore() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getCreatedBefore()));
        }

        if (criteria.getUpdatedAfter() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), criteria.getUpdatedAfter()));
        }

        if (criteria.getUpdatedBefore() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), criteria.getUpdatedBefore()));
        }
    }

    /**
     * Add creator filters
     */
    private static void addCreatorFilters(Integer createdById, String createdByEmail,
                                          Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (createdById != null) {
            predicates.add(cb.equal(root.get("createdBy").get("id"), createdById));
        }

        if (StringUtils.hasText(createdByEmail)) {
            predicates.add(cb.like(cb.lower(root.get("createdBy").get("email")), "%" + createdByEmail.toLowerCase() + "%"));
        }
    }

    /**
     * Add status filter
     */
    private static void addStatusFilter(Boolean isDeleted, Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        // Default: exclude deleted products
        predicates.add(cb.equal(root.get("isDeleted"), Objects.requireNonNullElse(isDeleted, false)));
    }

    /**
     * Add instance property filters
     */
    private static void addInstancePropertyFilters(Set<Integer> instancePropertyIds, Set<String> instancePropertyNames,
                                                   Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (instancePropertyIds != null && !instancePropertyIds.isEmpty()) {
            Join<Product, Stock> stockJoin = root.join("stocks", JoinType.INNER);
            Join<Stock, InstanceProperty> instanceJoin = stockJoin.join("instanceProperties", JoinType.INNER);
            predicates.add(instanceJoin.get("id").in(instancePropertyIds));
        }

        if (instancePropertyNames != null && !instancePropertyNames.isEmpty()) {
            Join<Product, Stock> stockJoin = root.join("stocks", JoinType.INNER);
            Join<Stock, InstanceProperty> instanceJoin = stockJoin.join("instanceProperties", JoinType.INNER);
            List<Predicate> instanceNamePredicates = new ArrayList<>();
            for (String instanceName : instancePropertyNames) {
                instanceNamePredicates.add(cb.like(cb.lower(instanceJoin.get("name")), "%" + instanceName.toLowerCase() + "%"));
            }
            predicates.add(cb.or(instanceNamePredicates.toArray(new Predicate[0])));
        }
    }

    /**
     * Add stock availability filter
     */
    private static void addStockAvailabilityFilter(Boolean inStock, Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (inStock != null && inStock) {
            Join<Product, Stock> stockJoin = root.join("stocks", JoinType.INNER);
            predicates.add(cb.greaterThan(stockJoin.get("quantity"), 0));
        }
    }

    /**
     * Add promotion filter
     */
    private static void addPromotionFilter(Set<Integer> promotionIds, Root<Product> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (promotionIds != null && !promotionIds.isEmpty()) {
            Join<Product, Promotion> promotionJoin = root.join("promotions", JoinType.INNER);
            predicates.add(promotionJoin.get("id").in(promotionIds));
        }
    }

    /**
     * Create sorting specification
     */
    public static Specification<Product> createSortSpecification(String sortBy, String sortDirection) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(sortBy)) {
                Order order;

                // Handle nested property sorting
                switch (sortBy.toLowerCase()) {
                    case "categoryname":
                        Path<?> categoryPath = root.get("category").get("name");
                        order = "desc".equalsIgnoreCase(sortDirection)
                                ? criteriaBuilder.desc(categoryPath)
                                : criteriaBuilder.asc(categoryPath);
                        query.orderBy(order);
                        break;
                    case "createdbyname":
                        Expression<String> createdByNameExpr = criteriaBuilder.concat(
                                criteriaBuilder.concat(root.get("createdBy").get("firstName"), " "),
                                root.get("createdBy").get("lastName")
                        );
                        order = "desc".equalsIgnoreCase(sortDirection)
                                ? criteriaBuilder.desc(createdByNameExpr)
                                : criteriaBuilder.asc(createdByNameExpr);
                        query.orderBy(order);
                        break;
                    case "price":
                        // For price sorting, we need to join with stocks and use MIN aggregation
                        Join<Product, Stock> stockJoin = root.join("stocks", JoinType.LEFT);
                        Expression<java.math.BigDecimal> minPriceExpr = criteriaBuilder.min(stockJoin.get("price"));
                        order = "desc".equalsIgnoreCase(sortDirection)
                                ? criteriaBuilder.desc(minPriceExpr)
                                : criteriaBuilder.asc(minPriceExpr);
                        query.groupBy(root.get("id")); // Group by product ID for aggregation
                        query.orderBy(order);
                        break;
                    case "quantity":
                        // For quantity sorting, sum all stock quantities
                        Join<Product, Stock> stockJoinQty = root.join("stocks", JoinType.LEFT);
                        Expression<Long> sumQuantityExpr = criteriaBuilder.sum(stockJoinQty.get("quantity"));
                        order = "desc".equalsIgnoreCase(sortDirection)
                                ? criteriaBuilder.desc(sumQuantityExpr)
                                : criteriaBuilder.asc(sumQuantityExpr);
                        query.groupBy(root.get("id"));
                        query.orderBy(order);
                        break;
                    default:
                        // Handle direct properties
                        try {
                            Path<?> directPath = root.get(sortBy);
                            order = "desc".equalsIgnoreCase(sortDirection)
                                    ? criteriaBuilder.desc(directPath)
                                    : criteriaBuilder.asc(directPath);
                            query.orderBy(order);
                        } catch (IllegalArgumentException e) {
                            // Default to name if property doesn't exist
                            Path<?> namePath = root.get("name");
                            order = criteriaBuilder.asc(namePath);
                            query.orderBy(order);
                        }
                }
            }

            return criteriaBuilder.conjunction(); // Return empty predicate for sorting-only spec
        };
    }
}
