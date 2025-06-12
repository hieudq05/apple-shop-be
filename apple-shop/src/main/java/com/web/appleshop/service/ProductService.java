package com.web.appleshop.service;

import com.web.appleshop.dto.request.CreateProductRequest;
import com.web.appleshop.dto.response.ProductAdminResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    void createProduct(CreateProductRequest request);
    Page<ProductAdminResponse> getAllProductsForAdmin(Pageable pageable);
}
