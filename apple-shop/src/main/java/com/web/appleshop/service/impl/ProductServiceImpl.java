package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.CreateProductRequest;
import com.web.appleshop.dto.response.ProductAdminResponse;
import com.web.appleshop.entity.*;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.*;
import com.web.appleshop.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final InstancePropertyRepository instancePropertyRepository;

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Override
    public void createProduct(CreateProductRequest request) {

        User createdBy = userRepository.findByUsername(request.getCreatedBy()).orElseThrow(
                () -> new NotFoundException("User not found with identifier: " + request.getCreatedBy())
        );

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
            category = categoryRepository.save(category);
        }

        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setCreatedBy(createdBy);
        product.setCategory(category);
        product.setUpdatedBy(createdBy);

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
                        .products(new LinkedHashSet<>())
                        .build();
                feature = featureRepository.save(feature);
            }
            feature.addProduct(product);
        }

        product = productRepository.save(product);

        Set<Stock> stocks = new LinkedHashSet<>();
        for (CreateProductRequest.CreateProductStockRequest stockRequest : request.getStocks()) {
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

            Stock stock = new Stock();
            BeanUtils.copyProperties(stockRequest, stock);
            stock.setProduct(product);

            for (CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest instanceRequest : stockRequest.getInstanceProperties()) {
                InstanceProperty instanceProperty;
                if (instanceRequest.getId() != null) {
                    instanceProperty = instancePropertyRepository.findById(instanceRequest.getId()).orElseThrow(
                            () -> new NotFoundException("Instance property not found with id: " + instanceRequest.getId())
                    );
                } else {
                    instanceProperty = InstanceProperty.builder()
                            .name(instanceRequest.getName())
                            .createdBy(createdBy)
                            .stocks(new LinkedHashSet<>())
                            .build();
                    instanceProperty = instancePropertyRepository.save(instanceProperty);
                }
                instanceProperty.addStock(stock);
            }

            stock = stockRepository.save(stock);

            Set<ProductPhoto> productPhotos = new LinkedHashSet<>();
            for (CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest photoRequest : stockRequest.getProductPhotos()) {
                ProductPhoto productPhoto = new ProductPhoto();
                BeanUtils.copyProperties(photoRequest, productPhoto);
                productPhoto.setStock(stock);
                productPhotos.add(productPhoto);
            }

            if (!productPhotos.isEmpty()) {
                productPhotoRepository.saveAll(productPhotos);
                stock.setProductPhotos(productPhotos);
            }
            color.setStock(stock);
            color = colorRepository.save(color);
            stock.setColor(color);

            stocks.add(stock);
        }

        product.setStocks(stocks);
        productRepository.save(product);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Override
    public Page<ProductAdminResponse> getAllProductsForAdmin(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::convertProductToProductAdminResponse);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public ProductAdminResponse convertProductToProductAdminResponse(Product product) {

        User createdByEntity = product.getCreatedBy();
        ProductAdminResponse.ProductOwnerAdminResponse createdByDto = new ProductAdminResponse.ProductOwnerAdminResponse(
                createdByEntity.getId(),
                createdByEntity.getEmail(),
                createdByEntity.getFirstName(),
                createdByEntity.getLastName(),
                createdByEntity.getImage(),
                createdByEntity.getUsername()
        );

        User updatedByEntity = product.getUpdatedBy();
        ProductAdminResponse.ProductUpdatedAdminResponse updatedByDto = new ProductAdminResponse.ProductUpdatedAdminResponse(
                updatedByEntity.getId(),
                updatedByEntity.getEmail(),
                updatedByEntity.getFirstName(),
                updatedByEntity.getLastName(),
                updatedByEntity.getImage(),
                updatedByEntity.getUsername()
        );

        Category categoryEntity = product.getCategory();
        ProductAdminResponse.ProductCategoryAdminResponse categoryDto = new ProductAdminResponse.ProductCategoryAdminResponse(
                categoryEntity.getId(),
                categoryEntity.getName(),
                categoryEntity.getImage()
        );
        BeanUtils.copyProperties(categoryEntity, categoryDto);

        Set<ProductAdminResponse.ProductStockAdminResponse> stockDto = product.getStocks().stream()
                .map(stock -> new ProductAdminResponse.ProductStockAdminResponse(
                        stock.getId(),
                        stock.getQuantity(),
                        stock.getPrice()
                )).collect(Collectors.toSet());
        return new ProductAdminResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCreatedAt(),
                createdByDto,
                product.getUpdatedAt(),
                updatedByDto,
                categoryDto,
                stockDto
        );
    }

}
