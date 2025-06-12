package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.CreateProductRequest;
import com.web.appleshop.entity.Product;
import com.web.appleshop.repository.ProductRepository;
import com.web.appleshop.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Override
    public void createProduct(CreateProductRequest request) {
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        productRepository.save(product);
    }
}
