package com.web.appleshop.service;

import com.web.appleshop.dto.response.ProductUserResponse;
import com.web.appleshop.dto.response.admin.ProductAdminListDto;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

public interface ProductService {
    void createProduct(String productJson, Map<String, MultipartFile> files);

    void updateProduct(Integer categoryId, Integer productId, String productJson, Map<String, MultipartFile> files, Integer[] productPhotoDeletions, User updatedBy);

    Page<ProductAdminResponse> getAllProductsForAdmin(Pageable pageable);

    Page<ProductAdminListDto> getAllProductsForAdminV1(Pageable pageable);

    Page<ProductAdminListDto> getAllProductsForAdminV2(Pageable pageable);

    Page<ProductUserResponse> getProductsByCategoryIdForUser(Integer categoryId, Pageable pageable);

    ProductUserResponse getProductByProductIdForUser(Integer categoryId, Integer productId);

    ProductAdminResponse getProductByProductIdForAdmin(Integer categoryId, Integer productId);

    void toggleDeleteProduct(Integer categoryId, Integer productId);

    Page<Map<String, Object>> getTopProductSelling(Pageable pageable, LocalDateTime fromDate, LocalDateTime toDate);

    Page<Map<String, Object>> getSaleByCategory(Pageable pageable, LocalDateTime fromDate, LocalDateTime toDate);

    Page<Map<String, Object>> getSaleByColor(Pageable pageable, LocalDateTime fromDate, LocalDateTime toDate);
}
