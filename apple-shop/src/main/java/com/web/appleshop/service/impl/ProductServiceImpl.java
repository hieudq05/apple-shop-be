package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.CreateProductRequest;
import com.web.appleshop.entity.*;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.*;
import com.web.appleshop.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FeatureRepository featureRepository;
    private final ColorRepository colorRepository;
    private final StockRepository stockRepository;
    private final ProductPhotoRepository productPhotoRepository;

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Override
    public void createProduct(CreateProductRequest request) {
        // 1. Lưu User
        User createdBy = userRepository.findByUsername(request.getCreatedBy()).orElseThrow(
                () -> new NotFoundException("User not found with identifier: " + request.getCreatedBy())
        );

        // 2. Lưu Category (nếu là category mới)
        Category category;
        if (request.getCategory().getId() != null) {
            category = categoryRepository.findById(request.getCategory().getId()).orElseThrow(
                    () -> new NotFoundException("Category not found with id: " + request.getCategory().getId())
            );
        } else {
            category = Category.builder()
                    .name(request.getCategory().getName())
                    .image(request.getCategory().getImage())
                    .build();
            category = categoryRepository.save(category); // Lưu category mới
        }

        // 3. Lưu Features (nếu là feature mới)
        Set<Feature> features = new LinkedHashSet<>();
        for (CreateProductRequest.CreateProductFeatureRequest featureRequest : request.getFeatures()) {
            Feature feature;
            if (featureRequest.getId() != null) {
                feature = featureRepository.findById(featureRequest.getId()).orElseThrow(
                        () -> new NotFoundException("Feature not found with id: " + featureRequest.getId())
                );
            } else {
                feature = Feature.builder()
                        .name(featureRequest.getName())
                        .description(featureRequest.getDescription())
                        .image(featureRequest.getImage())
                        .createdBy(createdBy)
                        .build();
                feature = featureRepository.save(feature); // Lưu feature mới
            }
            features.add(feature);
        }

        // 4. Tạo và lưu Product
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setCreatedBy(createdBy);
        product.setCategory(category);
        product.setFeatures(features);
        product.setUpdatedBy(createdBy);
        product = productRepository.save(product); // Lưu product

        // 5. Lưu Colors và Stocks sau
        Set<Stock> stocks = new LinkedHashSet<>();
        for (CreateProductRequest.CreateProductStockRequest stockRequest : request.getStocks()) {
            // Lưu Color (nếu là color mới)
            Color color;
            if (stockRequest.getColor().getId() != null) {
                color = colorRepository.findById(stockRequest.getColor().getId()).orElseThrow(
                        () -> new NotFoundException("Color not found with id: " + stockRequest.getColor().getId())
                );
            } else {
                color = Color.builder()
                        .name(stockRequest.getColor().getName())
                        .hexCode(stockRequest.getColor().getHexCode())
                        .build();
            }

            // Tạo Stock
            Stock stock = new Stock();
            BeanUtils.copyProperties(stockRequest, stock);
            stock.setProduct(product);
            stock = stockRepository.save(stock); // Lưu stock

            // Tạo ProductPhotos sau khi stock đã được lưu
            Set<ProductPhoto> productPhotos = new LinkedHashSet<>();
            for (CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest photoRequest : stockRequest.getProductPhotos()) {
                ProductPhoto productPhoto = new ProductPhoto();
                BeanUtils.copyProperties(photoRequest, productPhoto);
                productPhoto.setStock(stock);
                productPhotos.add(productPhoto);
            }

            if (!productPhotos.isEmpty()) {
                productPhotoRepository.saveAll(productPhotos); // Lưu photos
                stock.setProductPhotos(productPhotos);
            }
            color.setStock(stock);
            color = colorRepository.save(color); // Lưu color
            stock.setColor(color);

            stocks.add(stock);
        }

        // Cập nhật product với stocks
        product.setStocks(stocks);
        productRepository.save(product);
    }
}
