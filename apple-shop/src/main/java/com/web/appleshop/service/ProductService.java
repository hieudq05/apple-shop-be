package com.web.appleshop.service;

import com.web.appleshop.dto.response.ProductAdminResponse;
import com.web.appleshop.dto.response.ProductUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ProductService {
    void createProduct(String productJson, Map<String, MultipartFile> files);
    Page<ProductAdminResponse> getAllProductsForAdmin(Pageable pageable);
    Page<ProductUserResponse> getProductsByCategoryIdForUser(Integer categoryId, Pageable pageable);
    ProductUserResponse getProductByProductIdForUser(Integer categoryId, Integer productId);
}
