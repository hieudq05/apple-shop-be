package com.web.appleshop.service;

import com.web.appleshop.dto.request.ProductSearchCriteriaAdmin;
import com.web.appleshop.dto.request.ProductSearchCriteriaUser;
import com.web.appleshop.dto.response.ProductUserResponse;
import com.web.appleshop.dto.response.admin.ProductAdminListDto;
import com.web.appleshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for product search functionality
 */
public interface ProductSearchService {
    
    /**
     * Search products for admin users with full criteria support
     */
    Page<ProductAdminListDto> searchProductsForAdmin(ProductSearchCriteriaAdmin criteria, Pageable pageable);
    
    /**
     * Search products for regular users (filtered for public access)
     */
    Page<ProductUserResponse> searchProductsForUser(ProductSearchCriteriaUser criteria, Pageable pageable);
    
    /**
     * Search products and return entity objects (for internal use)
     */
    Page<Product> searchProducts(ProductSearchCriteriaAdmin criteria, Pageable pageable);
    
    /**
     * Count products matching the criteria
     */
    long countProducts(ProductSearchCriteriaAdmin criteria);
    
    /**
     * Check if products exist with the given criteria
     */
    boolean existsProducts(ProductSearchCriteriaAdmin criteria);
}
