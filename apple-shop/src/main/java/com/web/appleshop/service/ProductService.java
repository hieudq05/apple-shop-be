package com.web.appleshop.service;

import com.web.appleshop.dto.request.CreateProductRequest;
import com.web.appleshop.dto.response.ProductAdminResponse;
import com.web.appleshop.dto.response.ProductUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    void createProduct(CreateProductRequest request);
    Page<ProductAdminResponse> getAllProductsForAdmin(Pageable pageable);
    Page<ProductUserResponse> getProductsByCategoryIdForUser(Integer categoryId, Pageable pageable);

    void deleteProductById(Integer id);
}
