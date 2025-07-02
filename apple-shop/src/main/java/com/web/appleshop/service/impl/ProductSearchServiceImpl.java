package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.ProductSearchCriteria;
import com.web.appleshop.dto.response.ProductUserResponse;
import com.web.appleshop.dto.response.admin.ProductAdminListDto;
import com.web.appleshop.entity.Product;
import com.web.appleshop.repository.ProductRepository;
import com.web.appleshop.service.ProductSearchService;
import com.web.appleshop.service.ProductService;
import com.web.appleshop.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Implementation of ProductSearchService
 * Provides flexible and extensible product search functionality
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ProductRepository productRepository;
    private final ProductService productService;

    @Override
    public Page<ProductAdminListDto> searchProductsForAdmin(ProductSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching products for admin with criteria: {}", criteria);

        // For admin list view, we use the optimized V1 method if criteria is simple
        if (isSimpleCriteria(criteria)) {
            return ((ProductServiceImpl) productService).getAllProductsForAdminV1(pageable);
        }

        // For complex search, use specification
        Specification<Product> spec = buildSpecification(criteria);
        Pageable sortedPageable = applySorting(criteria, pageable);
        
        Page<Product> products = productRepository.findAll(spec, sortedPageable);
        
        // Convert to DTO manually for complex searches
        return products.map(this::convertToAdminListDto);
    }

    @Override
    public Page<ProductUserResponse> searchProductsForUser(ProductSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching products for user with criteria: {}", criteria);

        // Ensure deleted products are excluded for users
        if (criteria.getIsDeleted() == null) {
            criteria.setIsDeleted(false);
        }

        Specification<Product> spec = buildSpecification(criteria);
        Pageable sortedPageable = applySorting(criteria, pageable);
        
        Page<Product> products = productRepository.findAll(spec, sortedPageable);
        
        return products.map(this::convertToUserResponse);
    }

    @Override
    public Page<Product> searchProducts(ProductSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching products (entities) with criteria: {}", criteria);

        Specification<Product> spec = buildSpecification(criteria);
        Pageable sortedPageable = applySorting(criteria, pageable);
        
        return productRepository.findAll(spec, sortedPageable);
    }

    @Override
    public long countProducts(ProductSearchCriteria criteria) {
        log.debug("Counting products with criteria: {}", criteria);

        Specification<Product> spec = buildSpecification(criteria);
        return productRepository.count(spec);
    }

    @Override
    public boolean existsProducts(ProductSearchCriteria criteria) {
        log.debug("Checking if products exist with criteria: {}", criteria);

        return countProducts(criteria) > 0;
    }

    /**
     * Build the main specification from criteria
     */
    private Specification<Product> buildSpecification(ProductSearchCriteria criteria) {
        Specification<Product> spec = ProductSpecification.createSpecification(criteria);
        
        // Add sorting if specified in criteria (as an alternative to Pageable sorting)
        if (StringUtils.hasText(criteria.getSortBy())) {
            Specification<Product> sortSpec = ProductSpecification.createSortSpecification(
                criteria.getSortBy(), criteria.getSortDirection()
            );
            spec = spec.and(sortSpec);
        }
        
        return spec;
    }

    /**
     * Apply sorting to pageable if specified in criteria
     */
    private Pageable applySorting(ProductSearchCriteria criteria, Pageable pageable) {
        if (!StringUtils.hasText(criteria.getSortBy())) {
            return pageable;
        }

        // Create new pageable with sorting from criteria
        org.springframework.data.domain.Sort.Direction direction = 
            "desc".equalsIgnoreCase(criteria.getSortDirection()) 
                ? org.springframework.data.domain.Sort.Direction.DESC 
                : org.springframework.data.domain.Sort.Direction.ASC;

        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(direction, criteria.getSortBy());
        
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    /**
     * Check if the criteria is simple enough to use optimized queries
     */
    private boolean isSimpleCriteria(ProductSearchCriteria criteria) {
        return criteria.getName() == null &&
               criteria.getDescription() == null &&
               criteria.getSearchKeyword() == null &&
               criteria.getCategoryId() == null &&
               criteria.getCategoryName() == null &&
               (criteria.getFeatureIds() == null || criteria.getFeatureIds().isEmpty()) &&
               (criteria.getFeatureNames() == null || criteria.getFeatureNames().isEmpty()) &&
               (criteria.getColorIds() == null || criteria.getColorIds().isEmpty()) &&
               (criteria.getColorNames() == null || criteria.getColorNames().isEmpty()) &&
               criteria.getMinPrice() == null &&
               criteria.getMaxPrice() == null &&
               criteria.getMinQuantity() == null &&
               criteria.getMaxQuantity() == null &&
               criteria.getCreatedAfter() == null &&
               criteria.getCreatedBefore() == null &&
               criteria.getUpdatedAfter() == null &&
               criteria.getUpdatedBefore() == null &&
               criteria.getCreatedById() == null &&
               criteria.getCreatedByEmail() == null &&
               (criteria.getInstancePropertyIds() == null || criteria.getInstancePropertyIds().isEmpty()) &&
               (criteria.getInstancePropertyNames() == null || criteria.getInstancePropertyNames().isEmpty()) &&
               criteria.getInStock() == null &&
               (criteria.getPromotionIds() == null || criteria.getPromotionIds().isEmpty()) &&
               (criteria.getIsDeleted() == null || !criteria.getIsDeleted());
    }

    /**
     * Convert Product entity to ProductAdminListDto
     */
    private ProductAdminListDto convertToAdminListDto(Product product) {
        return new ProductAdminListDto(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getCreatedAt(),
            product.getCreatedBy().getFirstName() + " " + product.getCreatedBy().getLastName(),
            product.getCategory().getId(),
            product.getCategory().getName()
        );
    }

    /**
     * Convert Product entity to ProductUserResponse
     */
    private ProductUserResponse convertToUserResponse(Product product) {
        return ((ProductServiceImpl) productService).convertProductToProductUserResponse(product);
    }
}
