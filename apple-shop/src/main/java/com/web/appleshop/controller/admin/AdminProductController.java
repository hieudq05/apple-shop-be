package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.request.AdminProductSearchCriteria;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.ProductAdminListDto;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.entity.User;
import com.web.appleshop.service.ProductSearchService;
import com.web.appleshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Handles administrative operations for products.
 * <p>
 * This controller provides comprehensive CRUD (Create, Read, Update, Delete)
 * functionalities, search capabilities, and statistical analysis for products
 * within the admin panel.
 */
@RestController
@RequestMapping("admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final ProductSearchService productSearchService;

    /**
     * Creates a new product with its details and associated images.
     *
     * @param productJson A JSON string representing the product's data.
     * @param files A map of multipart files, where keys correspond to image identifiers.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<String>> createProduct(
            @RequestPart("product") String productJson,
            @RequestParam Map<String, MultipartFile> files
    ) {
        productService.createProduct(productJson, files);
        return ResponseEntity.ok(ApiResponse.success(null, "Product created successfully"));
    }

    /**
     * Updates an existing product, including its details, images, and photo deletions.
     *
     * @param productId The ID of the product to update.
     * @param productJson A JSON string with the updated product data.
     * @param productPhotoDeletions An array of photo IDs to be deleted (optional).
     * @param files A map of new multipart files for the product (optional).
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping(consumes = {"multipart/form-data"}, path = "{productId}")
    public ResponseEntity<ApiResponse<String>> updateProduct(
            @PathVariable Integer productId,
            @RequestPart("product") String productJson,
            @RequestPart(value = "productPhotoDeletions", required = false) Integer[] productPhotoDeletions,
            @RequestParam(required = false) Map<String, MultipartFile> files
    ) {
        User updatedBy = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        productService.updateProduct(productId, productJson, files, productPhotoDeletions, updatedBy);
        return ResponseEntity.ok(ApiResponse.success(null, "Product updated successfully"));
    }

    /**
     * Retrieves a paginated list of all products for the admin panel.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of products per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of {@link ProductAdminListDto}.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductAdminListDto>>> getAllProducts(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 6, sort);
        Page<ProductAdminListDto> productAdminResponsePage = productService.getAllProductsForAdminV2(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                productAdminResponsePage.getNumber(),
                productAdminResponsePage.getSize(),
                productAdminResponsePage.getTotalPages(),
                productAdminResponsePage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(productAdminResponsePage.getContent(), "Get all products successfully", pageableResponse));
    }

    /**
     * Retrieves the details of a single product for the admin panel.
     *
     * @param productId The ID of the product to retrieve.
     * @return A {@link ResponseEntity} containing the detailed {@link ProductAdminResponse}.
     */
    @GetMapping("{productId}")
    public ResponseEntity<ApiResponse<ProductAdminResponse>> getProduct(@PathVariable Integer productId) {
        ProductAdminResponse productAdminResponse = productService.getProductByProductIdForAdmin(productId);
        return ResponseEntity.ok(ApiResponse.success(productAdminResponse, "Get product successfully"));
    }

    /**
     * Soft-deletes a product by toggling its `isDeleted` flag.
     *
     * @param productId The ID of the product to soft-delete.
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping("{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Integer productId) {
        productService.toggleDeleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Delete product successfully"));
    }

    /**
     * Permanently deletes a product from the database.
     *
     * @param productId The ID of the product to delete forever.
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping("delete-forever/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteForeverProduct(@PathVariable Integer productId) {
        productService.deleteForeverProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Delete product successfully"));
    }

    /**
     * Searches for products based on specified criteria for the admin panel.
     *
     * @param criteria The search criteria.
     * @return A {@link ResponseEntity} with a paginated list of found products.
     */
    @PostMapping("search")
    public ResponseEntity<ApiResponse<List<ProductAdminListDto>>> searchProduct(
            @RequestBody AdminProductSearchCriteria criteria
    ) {
        Pageable pageable = Pageable.ofSize(criteria.getSize() != null ? criteria.getSize() : 10).withPage(criteria.getPage() != null ? criteria.getPage() : 0);
        Page<ProductAdminListDto> productsPage = productSearchService.searchProductsForAdmin(criteria, pageable);
        PageableResponse pageableResponse = new PageableResponse(
                productsPage.getNumber(),
                productsPage.getSize(),
                productsPage.getTotalPages(),
                productsPage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(productsPage.getContent(), "Search products successfully", pageableResponse));
    }

    /**
     * Retrieves statistics on top-selling products.
     *
     * @param limit    The maximum number of top products to return (optional, defaults to 6).
     * @param fromDate The start date for the statistics (optional).
     * @param toDate   The end date for the statistics (optional).
     * @return A {@link ResponseEntity} containing a list of top-selling products.
     */
    @GetMapping("statistics/top-selling")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopSellingProducts(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        Pageable pageable = Pageable.ofSize(limit != null ? limit : 6);
        Page<Map<String, Object>> productsPage = productService.getTopProductSelling(pageable, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success(productsPage.getContent(), "Get top selling products successfully"));
    }

    /**
     * Retrieves sales statistics grouped by category.
     *
     * @param page     The page number for pagination (optional, defaults to 0).
     * @param size     The page size for pagination (optional, defaults to 6).
     * @param fromDate The start date for the statistics (optional).
     * @param toDate   The end date for the statistics (optional).
     * @return A {@link ResponseEntity} containing paginated sales data by category.
     */
    @GetMapping("statistics/selling-by-category")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSellingByCategory(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 6);
        Page<Map<String, Object>> productsPage = productService.getSaleByCategory(pageable, fromDate, toDate);
        PageableResponse pageableResponse = new PageableResponse(
                productsPage.getNumber(),
                productsPage.getSize(),
                productsPage.getTotalPages(),
                productsPage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(productsPage.getContent(), "Get selling by category successfully", pageableResponse));
    }

    /**
     * Retrieves sales statistics grouped by color.
     *
     * @param page     The page number for pagination (optional, defaults to 0).
     * @param size     The page size for pagination (optional, defaults to 6).
     * @param fromDate The start date for the statistics (optional).
     * @param toDate   The end date for the statistics (optional).
     * @return A {@link ResponseEntity} containing paginated sales data by color.
     */
    @GetMapping("statistics/selling-by-color")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSellingByColor(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 6);
        Page<Map<String, Object>> productsPage = productService.getSaleByColor(pageable, fromDate, toDate);
        PageableResponse pageableResponse = new PageableResponse(
                productsPage.getNumber(),
                productsPage.getSize(),
                productsPage.getTotalPages(),
                productsPage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(productsPage.getContent(), "Get selling by color successfully", pageableResponse));
    }
}
