package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.AdminProductSearchCriteria;
import com.web.appleshop.dto.request.BaseProductSearchCriteria;
import com.web.appleshop.dto.request.UserProductSearchCriteria;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Page<ProductAdminListDto> searchProductsForAdmin(AdminProductSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching products for admin with criteria: {}", criteria);

        if (isSimpleCriteria(criteria)) {
            return productService.getAllProductsForAdminV1(pageable);
        }

        Specification<Product> spec = buildSpecification(criteria);
//        Pageable sortedPageable = applySorting(criteria, pageable);

        Page<Product> products = productRepository.findAll(spec, pageable);

        return products.map(this::convertToAdminListDto);
    }

    @Override
    public Page<ProductUserResponse> searchProductsForUser(UserProductSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching products for user with criteria: {}", criteria);

        Specification<Product> spec = buildSpecification(criteria);
//        Pageable sortedPageable = applySorting(criteria, pageable);

        Page<Product> products = productRepository.findAll(spec, pageable);

        return products.map(this::convertToUserResponse);
    }

    @Override
    public Page<Product> searchProducts(AdminProductSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching products (entities) with criteria: {}", criteria);

        Specification<Product> spec = buildSpecification(criteria);
        Pageable sortedPageable = applySorting(criteria, pageable);

        return productRepository.findAll(spec, sortedPageable);
    }

    @Override
    public long countProducts(AdminProductSearchCriteria criteria) {
        log.debug("Counting products with criteria: {}", criteria);

        Specification<Product> spec = buildSpecification(criteria);
        return productRepository.count(spec);
    }

    @Override
    public boolean existsProducts(AdminProductSearchCriteria criteria) {
        log.debug("Checking if products exist with criteria: {}", criteria);

        return countProducts(criteria) > 0;
    }

    /**
     * Build the admin specification from criteria
     */
    private <T extends BaseProductSearchCriteria> Specification<Product> buildSpecification(T criteria) {
        Specification<Product> spec = null;

        log.info("Criteria type: {}", criteria.getClass().getSimpleName());

        if (criteria instanceof AdminProductSearchCriteria criteriaAdmin) {
            spec = ProductSpecification.createSpecification(criteriaAdmin);
        } else if (criteria instanceof UserProductSearchCriteria criteriaUser) {
            spec = ProductSpecification.createSpecification(criteriaUser);
        }

        // Add sorting if specified in criteria (as an alternative to Pageable sorting)
//        if (StringUtils.hasText(criteria.getSortBy())) {
//            Specification<Product> sortSpec = ProductSpecification.createSortSpecification(
//                    criteria.getSortBy(), criteria.getSortDirection()
//            );
//            assert spec != null;
//            spec = spec.and(sortSpec);
//        }

        return spec;
    }

    /**
     * Build the user specification from criteria
     */
    private Specification<Product> buildSpecificationUser(UserProductSearchCriteria criteria) {
        Specification<Product> spec = ProductSpecification.createSpecification(criteria);

        // Add sorting if specified in criteria (as an alternative to Pageable sorting)
//        if (StringUtils.hasText(criteria.getSortBy())) {
//            Specification<Product> sortSpec = ProductSpecification.createSortSpecification(
//                    criteria.getSortBy(), criteria.getSortDirection()
//            );
//            spec = spec.and(sortSpec);
//        }
        return spec;
    }

    /**
     * Apply sorting to pageable if specified in criteria
     */
    private Pageable applySorting(AdminProductSearchCriteria criteria, Pageable pageable) {
        String sortBy = criteria.getSortBy().toLowerCase();
        Set<String> directProductFields = Set.of("id", "name", "createdat", "updatedat");

        if (directProductFields.contains(sortBy)) {
            Sort.Direction direction =
                    "desc".equalsIgnoreCase(criteria.getSortDirection())
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC;

            Sort sort = Sort.by(direction, criteria.getSortBy());
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        // For complex sorting (price, categoryName, etc.), let Specification handle it
        return pageable;
    }

    /**
     * Check if the criteria is simple enough to use optimized queries
     */
    private boolean isSimpleCriteria(AdminProductSearchCriteria criteria) {
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
        return ((ProductServiceImpl) productService).convertProductToProductAdminListDto(product);
    }

    /**
     * Convert Product entity to ProductUserResponse
     */
    private ProductUserResponse convertToUserResponse(Product product) {
        return ((ProductServiceImpl) productService).convertProductToProductUserResponse(product);
    }
}
