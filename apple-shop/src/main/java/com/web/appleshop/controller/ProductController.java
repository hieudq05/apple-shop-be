package com.web.appleshop.controller;

import com.web.appleshop.dto.request.UserProductSearchCriteria;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.ProductUserResponse;
import com.web.appleshop.service.ProductSearchService;
import com.web.appleshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles public-facing HTTP requests related to products.
 * <p>
 * This controller provides endpoints for users to browse, search, and view
 * products. It includes functionality to get products by category, find top-selling
 * items, view detailed product information, and perform complex searches.
 */
@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductSearchService productSearchService;

    /**
     * Retrieves a paginated list of products for a specific category.
     *
     * @param categoryId The ID of the category.
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of products per page (optional, defaults to 6).
     * @param sortField The field to sort by (optional, defaults to "createdAt").
     * @return A {@link ResponseEntity} containing a paginated list of {@link ProductUserResponse}.
     */
    @GetMapping("{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductUserResponse>>> getAllProducts(
            @PathVariable Integer categoryId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField
    ) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 6, Sort.by(Sort.Direction.DESC, sortField != null ? sortField : "createdAt"));
        Page<ProductUserResponse> productUserResponsePage = productService.getProductsByCategoryIdForUser(categoryId, pageable);
        PageableResponse pageableResponse = new PageableResponse(
                productUserResponsePage.getNumber(),
                productUserResponsePage.getSize(),
                productUserResponsePage.getTotalPages(),
                productUserResponsePage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(productUserResponsePage.getContent(), "Get all products successfully", pageableResponse));
    }

    /**
     * Retrieves a paginated list of top-selling products for a specific category.
     *
     * @param categoryId The ID of the category.
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of products per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of top-selling {@link ProductUserResponse}.
     */
    @GetMapping("{categoryId}/top_selling")
    public ResponseEntity<ApiResponse<List<ProductUserResponse>>> getTopSellingProducts(
            @PathVariable Integer categoryId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<ProductUserResponse> productUserResponsePage = productService.getTopProductsByCategoryIdForUser(categoryId, pageable);
        PageableResponse pageableResponse = new PageableResponse(
                productUserResponsePage.getNumber(),
                productUserResponsePage.getSize(),
                productUserResponsePage.getTotalPages(),
                productUserResponsePage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(productUserResponsePage.getContent(), "Get top selling products successfully", pageableResponse));
    }

    /**
     * Retrieves a single product by its ID within a given category.
     *
     * @param categoryId The ID of the category.
     * @param productId The ID of the product to retrieve.
     * @return A {@link ResponseEntity} containing the {@link ProductUserResponse}.
     */
    @GetMapping("{categoryId}/{productId}")
    public ResponseEntity<ApiResponse<ProductUserResponse>> getProduct(@PathVariable Integer categoryId, @PathVariable Integer productId) {
        ProductUserResponse productUserResponse = productService.getProductByProductIdForUser(categoryId, productId);
        return ResponseEntity.ok(ApiResponse.success(productUserResponse, "Get product successfully"));
    }

    /**
     * Searches for products based on user-defined criteria.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of products per page (optional, defaults to 6).
     * @param criteria The search criteria.
     * @return A {@link ResponseEntity} containing a paginated list of matching {@link ProductUserResponse}.
     */
    @PostMapping("search")
    public ResponseEntity<ApiResponse<List<ProductUserResponse>>> searchProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestBody UserProductSearchCriteria criteria
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<ProductUserResponse> productsPage = productSearchService.searchProductsForUser(criteria, pageable);
        PageableResponse pageableResponse = new PageableResponse(
                productsPage.getNumber(),
                productsPage.getSize(),
                productsPage.getTotalPages(),
                productsPage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(productsPage.getContent(), "Search products successfully", pageableResponse));
    }
}
