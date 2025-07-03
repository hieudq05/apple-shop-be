package com.web.appleshop.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.appleshop.dto.request.CreateProductRequest;
import com.web.appleshop.dto.request.ProductSearchCriteria;
import com.web.appleshop.dto.request.UpdateProductRequest;
import com.web.appleshop.dto.response.ProductUserResponse;
import com.web.appleshop.dto.response.ValidationErrorDetail;
import com.web.appleshop.dto.response.admin.*;
import com.web.appleshop.entity.*;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.exception.ValidationException;
import com.web.appleshop.repository.*;
import com.web.appleshop.service.ProductService;
import com.web.appleshop.specification.ProductSpecification;
import com.web.appleshop.util.UploadUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final Validator validator;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FeatureRepository featureRepository;
    private final ColorRepository colorRepository;
    private final StockRepository stockRepository;
    private final ProductPhotoRepository productPhotoRepository;
    private final InstancePropertyRepository instancePropertyRepository;
    private final UploadUtils uploadUtils;

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Override
    public void createProduct(String requestData, Map<String, MultipartFile> files) {

        CreateProductRequest request = convertRequestDataToCreateProductRequest(requestData, files);

        User createdBy = userRepository.getUserByEmail(request.getCreatedBy()).orElseThrow(() -> new NotFoundException("User not found with identifier: " + request.getCreatedBy()));

        Category category;
        if (request.getCategory().getId() != null) {
            category = categoryRepository.findById(request.getCategory().getId()).orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategory().getId()));
        } else {
            category = Category.builder()
                    .name(request.getCategory().getName())
                    .image(
                            request.getCategory().getImage() instanceof MultipartFile
                                    ? uploadUtils.uploadFile((MultipartFile) request.getCategory().getImage())
                                    : request.getCategory().getImage().toString()
                    )
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
                feature = featureRepository.findById(featureRequest.getId()).orElseThrow(() -> new NotFoundException("Feature not found with id: " + featureRequest.getId()));
            } else {
                feature = Feature.builder()
                        .name(featureRequest.getName())
                        .description(featureRequest.getDescription())
                        .image(
                                featureRequest.getImage() instanceof MultipartFile
                                        ? uploadUtils.uploadFile((MultipartFile) featureRequest.getImage())
                                        : featureRequest.getImage().toString()
                        )
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
                color = colorRepository.findById(stockRequest.getColor().getId()).orElseThrow(() -> new NotFoundException("Color not found with id: " + stockRequest.getColor().getId()));
            } else {
                color = Color.builder().name(stockRequest.getColor().getName()).hexCode(stockRequest.getColor().getHexCode()).build();
            }
            color = colorRepository.save(color);

            Stock stock = new Stock();
            BeanUtils.copyProperties(stockRequest, stock);
            stock.setProduct(product);

            Set<InstanceProperty> instanceProperties = stockRequest.getInstanceProperties().stream().map(instanceRequest -> {
                InstanceProperty instanceProperty;
                if (instanceRequest.getId() != null) {
                    instanceProperty = instancePropertyRepository.findById(instanceRequest.getId())
                            .orElseThrow(() -> new NotFoundException("Instance property not found with id: " + instanceRequest.getId()));
                    instanceProperty.setName(instanceRequest.getName());
                } else {
                    instanceProperty = InstanceProperty.builder()
                            .name(instanceRequest.getName())
                            .createdBy(createdBy)
                            .createdAt(LocalDateTime.now())
                            .build();
                    instanceProperty = instancePropertyRepository.save(instanceProperty); // Cần save vì là entity mới
                }
                return instanceProperty;
            }).collect(Collectors.toSet());
            stock.setInstanceProperties(instanceProperties);

            stock = stockRepository.save(stock);

            Set<ProductPhoto> productPhotos = new LinkedHashSet<>();
            for (CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest photoRequest : stockRequest.getProductPhotos()) {
                ProductPhoto productPhoto = new ProductPhoto();
                productPhoto.setImageUrl(
                        photoRequest.getImageUrl() instanceof MultipartFile
                                ? uploadUtils.uploadFile((MultipartFile) photoRequest.getImageUrl())
                                : photoRequest.getImageUrl().toString()
                );
                productPhoto.setAlt(photoRequest.getAlt());
                productPhoto.setStock(stock);
                productPhotos.add(productPhoto);
            }

            if (!productPhotos.isEmpty()) {
                productPhotoRepository.saveAll(productPhotos);
                stock.setProductPhotos(productPhotos);
            }
            stock.setColor(color);

            stocks.add(stock);
        }

        product.setStocks(stocks);

        productRepository.save(product);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Override
    public void updateProduct(Integer categoryId, Integer productId, String productJson, Map<String, MultipartFile> files, Integer[] productPhotoDeletions, User updatedBy) {
        // 1. Tải các entity gốc từ DB
        Product product = productRepository.findProductByIdAndCategory_Id(productId, categoryId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId + " and category id: " + categoryId));

        User persistentUpdatedBy = userRepository.getUserByEmail(updatedBy.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + updatedBy.getEmail()));

        // 2. Chuyển đổi JSON string thành DTO
        ObjectMapper objectMapper = new ObjectMapper();
        UpdateProductRequest productRequest;
        try {
            productRequest = objectMapper.readValue(productJson, UpdateProductRequest.class);

            // Validate the request
            Set<ConstraintViolation<UpdateProductRequest>> violations = validator.validate(productRequest);
            if (!violations.isEmpty()) {
                List<ValidationErrorDetail> validationErrors = new ArrayList<>();
                for (ConstraintViolation<UpdateProductRequest> violation : violations) {
                    validationErrors.add(new ValidationErrorDetail(
                            violation.getPropertyPath().toString(),
                            violation.getMessage()
                    ));
                }
                throw new ValidationException("Lỗi dữ liệu sản phẩm", validationErrors);
            }
        } catch (JsonProcessingException e) {
            log.info(e.getMessage());
            throw new BadRequestException("Lỗi khi chuyển đổi dữ liệu sản phẩm.");
        }

        // 3. Cập nhật các thuộc tính của Product
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setUpdatedAt(LocalDateTime.now());
        product.setUpdatedBy(persistentUpdatedBy);

        // 4. Xử lý Category (Logic gốc của bạn)
        Category category;
        if (productRequest.getCategory().getId() != null) {
            category = categoryRepository.findById(productRequest.getCategory().getId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + productRequest.getCategory().getId()));
            category.setName(productRequest.getCategory().getName());
            category.setImage(
                    productRequest.getCategory().getImage() instanceof MultipartFile
                            ? uploadUtils.uploadFile((MultipartFile) productRequest.getCategory().getImage())
                            : productRequest.getCategory().getImage().toString()
            );
        } else {
            category = Category.builder()
                    .name(productRequest.getCategory().getName())
                    .image(
                            productRequest.getCategory().getImage() instanceof MultipartFile
                                    ? uploadUtils.uploadFile((MultipartFile) productRequest.getCategory().getImage())
                                    : productRequest.getCategory().getImage().toString()
                    )
                    .build();
            category = categoryRepository.save(category); // Cần save vì là entity mới
        }
        product.setCategory(category);

        // 5. SỬA LỖI: Xử lý collection Features
        Set<Feature> managedFeatures = product.getFeatures();
        managedFeatures.clear(); // Xóa các mối quan hệ cũ

        Set<Feature> newFeaturesToAdd = productRequest.getFeatures().stream().map(featureRequest -> {
            Feature feature;
            if (featureRequest.getId() != null) {
                feature = featureRepository.findById(featureRequest.getId())
                        .orElseThrow(() -> new NotFoundException("Feature not found with id: " + featureRequest.getId()));
            } else {
                feature = Feature.builder()
                        .name(featureRequest.getName())
                        .description(featureRequest.getDescription())
                        .image(
                                featureRequest.getImage() instanceof MultipartFile
                                        ? uploadUtils.uploadFile((MultipartFile) featureRequest.getImage())
                                        : featureRequest.getImage().toString()
                        )
                        .createdBy(persistentUpdatedBy)
                        .products(new LinkedHashSet<>())
                        .build();
                feature = featureRepository.save(feature); // Cần save vì là entity mới
            }
            feature.addProduct(product);
            return feature;
        }).collect(Collectors.toSet());

        managedFeatures.addAll(newFeaturesToAdd); // Thêm các mối quan hệ mới vào collection đang được quản lý

        // 6. SỬA LỖI: Xử lý collection Stocks
        Set<Stock> managedStocks = product.getStocks();
        managedStocks.clear(); // Xóa các stock cũ. Do có orphanRemoval=true, Hibernate sẽ xóa chúng khỏi DB.

        Set<Stock> newStocksToAdd = productRequest.getStocks().stream().map(stockRequest -> {
            Stock stock;
            if (stockRequest.getId() != null) {
                stock = stockRepository.findById(stockRequest.getId()).orElseGet(Stock::new);
            } else {
                stock = new Stock();
            }

            stock.setProduct(product); // Luôn thiết lập mối quan hệ ngược lại
            stock.setQuantity(stockRequest.getQuantity());
            stock.setPrice(stockRequest.getPrice());

            // Xử lý Color (Logic gốc của bạn)
            Color color;
            if (stockRequest.getColor().getId() != null) {
                color = colorRepository.findById(stockRequest.getColor().getId())
                        .orElseThrow(() -> new NotFoundException("Color not found with id: " + stockRequest.getColor().getId()));
                color.setName(stockRequest.getColor().getName());
                color.setHexCode(stockRequest.getColor().getHexCode());
            } else {
                color = Color.builder()
                        .name(stockRequest.getColor().getName())
                        .hexCode(stockRequest.getColor().getHexCode())
                        .build();
                color = colorRepository.save(color); // Cần save vì là entity mới
            }
            stock.setColor(color);

            // Xử lý InstanceProperty (Logic gốc của bạn)
            Set<InstanceProperty> instanceProperties = stockRequest.getInstanceProperties().stream().map(instanceRequest -> {
                InstanceProperty instanceProperty;
                if (instanceRequest.getId() != null) {
                    instanceProperty = instancePropertyRepository.findById(instanceRequest.getId())
                            .orElseThrow(() -> new NotFoundException("Instance property not found with id: " + instanceRequest.getId()));
                    instanceProperty.setName(instanceRequest.getName());
                } else {
                    instanceProperty = InstanceProperty.builder()
                            .name(instanceRequest.getName())
                            .createdBy(persistentUpdatedBy)
                            .createdAt(LocalDateTime.now())
                            .build();
                    instanceProperty = instancePropertyRepository.save(instanceProperty); // Cần save vì là entity mới
                }
                return instanceProperty;
            }).collect(Collectors.toSet());

            stock.setInstanceProperties(instanceProperties);

            // SỬA LỖI: Xử lý collection ProductPhotos lồng bên trong
            Set<ProductPhoto> managedPhotos = stock.getProductPhotos();
            managedPhotos.clear();

            Set<ProductPhoto> newPhotosToAdd = stockRequest.getProductPhotos().stream().map(photoRequest -> {
                ProductPhoto productPhoto;
                if (photoRequest.getId() != null) {
                    productPhoto = productPhotoRepository.findById(photoRequest.getId())
                            .orElseThrow(() -> new NotFoundException("Product photo not found with id: " + photoRequest.getId()));
                } else {
                    productPhoto = new ProductPhoto();
                }
                productPhoto.setStock(stock); // Thiết lập mối quan hệ ngược
                productPhoto.setImageUrl(
                        photoRequest.getImageUrl() instanceof MultipartFile
                                ? uploadUtils.uploadFile((MultipartFile) photoRequest.getImageUrl())
                                : photoRequest.getImageUrl().toString()
                );
                productPhoto.setAlt(photoRequest.getAlt());
                return productPhoto;
            }).collect(Collectors.toSet());

            managedPhotos.addAll(newPhotosToAdd);

            return stock;
        }).collect(Collectors.toSet());

        managedStocks.addAll(newStocksToAdd);
    }

    @Deprecated(forRemoval = true)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional(readOnly = true)
    @Override
    public Page<ProductAdminResponse> getAllProductsForAdmin(Pageable pageable) {
        Page<Product> products = productRepository.findAllWithRelationships(pageable);
        return products.map(this::convertProductToProductAdminResponse);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional(readOnly = true)
    @Override
    public Page<ProductAdminListDto> getAllProductsForAdminV1(Pageable pageable) {
        Page<ProductAdminListDto> productsPage = productRepository.findProductAdminList(pageable);
        List<ProductAdminListDto> productsList = productsPage.getContent();

        if (productsList.isEmpty()) {
            return productsPage;
        }

        Map<Integer, ProductAdminListDto> productDtoMap = productsList.stream()
                .collect(Collectors.toMap(ProductAdminListDto::getId, dto -> dto));
        List<Integer> productIds = new ArrayList<>(productDtoMap.keySet());

        Set<ProductFeatureDto> productFeatures = productRepository.findFeaturesForProducts(productIds);

        for (ProductFeatureDto pfDto : productFeatures) {
            // Tìm product DTO cha dựa vào productId trong DTO trung gian
            ProductAdminListDto parentProduct = productDtoMap.get(pfDto.productId());
            if (parentProduct != null) {
                // Tạo DTO feature cuối cùng và thêm vào set của product cha
                parentProduct.getFeatures().add(
                        new FeatureSummaryDto(pfDto.featureId(), pfDto.featureName(), pfDto.featureImage())
                );
            }
        }

        Set<StockSummaryDto> stockSummaries = stockRepository.findStockSummariesForProducts(productIds);
        Map<Integer, StockSummaryDto> stockDtoMap = new HashMap<>();
        for (StockSummaryDto stockDto : stockSummaries) {
            stockDtoMap.put(stockDto.getId(), stockDto);
            ProductAdminListDto parentProduct = productDtoMap.get(stockDto.getProductId());
            if (parentProduct != null) {
                parentProduct.getStocks().add(stockDto);
            }
        }

        Set<Integer> stockIds = stockDtoMap.keySet();
        if (stockIds.isEmpty()) {
            return new PageImpl<>(productsList, pageable, productsPage.getTotalElements());
        }

        Set<StockPhotoDtoLink> photoLinks = productPhotoRepository.findPhotosForStocks(stockIds);
        for (StockPhotoDtoLink link : photoLinks) {
            StockSummaryDto parentStock = stockDtoMap.get(link.stockId());
            if (parentStock != null) {
                parentStock.getProductPhotos().add(
                        new StockSummaryDto.ProductPhotoDto(link.photoId(), link.imageUrl(), link.alt())
                );
            }
        }

        Set<StockInstanceDto> stockProperties = instancePropertyRepository.findInstancesForProducts(stockIds);
        for (StockInstanceDto spDto : stockProperties) {
            StockSummaryDto parentStock = stockDtoMap.get(spDto.stockId());
            if (parentStock != null) {
                parentStock.getInstanceProperties().add(
                        new StockSummaryDto.InstancePropertyDto(spDto.instanceId(), spDto.instanceName())
                );
            }
        }

        return new PageImpl<>(productsList, pageable, productsPage.getTotalElements());
    }

    @Override
    public Page<ProductAdminListDto> getAllProductsForAdminV2(Pageable pageable) {
        Specification<Product> spec = ProductSpecification.createSpecification(
                ProductSearchCriteria.builder().build()
        );
        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(this::convertProductToProductAdminListDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductUserResponse> getProductsByCategoryIdForUser(Integer categoryId, Pageable pageable) {
        Page<Product> products = productRepository.findAllByCategory_IdAndIsDeleted(categoryId, false, pageable).orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId));
        return products.map(this::convertProductToProductUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductUserResponse getProductByProductIdForUser(Integer categoryId, Integer productId) {
        Product product = productRepository.findProductByIdAndCategory_IdAndIsDeleted(productId, categoryId, false).orElseThrow(
                () -> new NotFoundException("Sản phẩm với id: " + productId + " và danh mục với id: " + categoryId + " không tồn tại.")
        );
        return convertProductToProductUserResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public ProductAdminResponse getProductByProductIdForAdmin(Integer categoryId, Integer productId) {
        Product product = productRepository.findProductByIdAndCategory_Id(productId, categoryId).orElseThrow(
                () -> new NotFoundException("Sản phẩm với id: " + productId + " và danh mục với id: " + categoryId + " không tồn tại.")
        );
        return convertProductToProductAdminResponse(product);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public void deleteProduct(Integer categoryId, Integer productId) {
        Product product = productRepository.findProductByIdAndCategory_Id(productId, categoryId).orElseThrow(
                () -> new NotFoundException("Sản phẩm với id: " + productId + " và danh mục với id: " + categoryId + " không tồn tại.")
        );
        product.setIsDeleted(true);
        productRepository.save(product);
    }

    public ProductUserResponse convertProductToProductUserResponse(Product product) {
        Set<ProductUserResponse.ProductStockResponse> stockDtos = new LinkedHashSet<>();
        for (Stock stock : product.getStocks()) {
            ProductUserResponse.ProductStockResponse.StockColorResponse colorDto = new ProductUserResponse.ProductStockResponse.StockColorResponse(stock.getColor().getId(), stock.getColor().getName(), stock.getColor().getHexCode());
            Set<ProductUserResponse.ProductStockResponse.StockPhotoResponse> photoDtos = stock.getProductPhotos().stream().map(photo -> new ProductUserResponse.ProductStockResponse.StockPhotoResponse(photo.getId(), photo.getImageUrl(), photo.getAlt())).collect(Collectors.toSet());
            Set<ProductUserResponse.ProductStockResponse.StockInstanceResponse> instanceDto = stock.getInstanceProperties().stream().map(instanceProperty -> new ProductUserResponse.ProductStockResponse.StockInstanceResponse(instanceProperty.getId(), instanceProperty.getName())).collect(Collectors.toSet());
            stockDtos.add(new ProductUserResponse.ProductStockResponse(stock.getId(), colorDto, stock.getQuantity(), stock.getPrice(), photoDtos, instanceDto));
        }

        return new ProductUserResponse(product.getId(), product.getName(), product.getDescription(), stockDtos);
    }

    public ProductAdminListDto convertProductToProductAdminListDto(Product product) {
        return ProductAdminListDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .createdAt(product.getCreatedAt())
                .createdBy(product.getCreatedBy().getFirstName() + " " + product.getCreatedBy().getLastName())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .features(product.getFeatures().stream().map(feature ->
                        FeatureSummaryDto.builder()
                                .id(feature.getId())
                                .name(feature.getName())
                                .image(feature.getImage())
                                .build()
                ).collect(Collectors.toSet()))
                .stocks(product.getStocks().stream().map(stock ->
                        StockSummaryDto.builder()
                                .id(stock.getId())
                                .quantity(stock.getQuantity())
                                .price(stock.getPrice())
                                .productPhotos(stock.getProductPhotos().stream().map(photo ->
                                        StockSummaryDto.ProductPhotoDto.builder()
                                                .id(photo.getId())
                                                .imageUrl(photo.getImageUrl())
                                                .alt(photo.getAlt())
                                                .build()
                                ).collect(Collectors.toSet()))
                                .instanceProperties(stock.getInstanceProperties().stream().map(instanceProperty ->
                                        StockSummaryDto.InstancePropertyDto.builder()
                                                .id(instanceProperty.getId())
                                                .name(instanceProperty.getName())
                                                .build()
                                ).collect(Collectors.toSet()))
                                .colorId(stock.getColor().getId())
                                .colorName(stock.getColor().getName())
                                .colorHexCode(stock.getColor().getHexCode())
                                .build()
                ).collect(Collectors.toSet()))
                .build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public ProductAdminResponse convertProductToProductAdminResponse(Product product) {
        User createdByEntity = product.getCreatedBy();
        ProductAdminResponse.ProductOwnerAdminResponse createdByDto = new ProductAdminResponse.ProductOwnerAdminResponse(createdByEntity.getId(), createdByEntity.getEmail(), createdByEntity.getFirstName(), createdByEntity.getLastName(), createdByEntity.getImage());

        User updatedByEntity = product.getUpdatedBy();
        ProductAdminResponse.ProductUpdatedAdminResponse updatedByDto = new ProductAdminResponse.ProductUpdatedAdminResponse(updatedByEntity.getId(), updatedByEntity.getEmail(), updatedByEntity.getFirstName(), updatedByEntity.getLastName(), updatedByEntity.getImage());

        Category categoryEntity = product.getCategory();
        ProductAdminResponse.ProductCategoryAdminResponse categoryDto = new ProductAdminResponse.ProductCategoryAdminResponse(categoryEntity.getId(), categoryEntity.getName(), categoryEntity.getImage());
        BeanUtils.copyProperties(categoryEntity, categoryDto);

        Set<ProductAdminResponse.ProductStockAdminResponse> stockDto = product.getStocks().stream().map(stock -> new ProductAdminResponse.ProductStockAdminResponse(
                stock.getId(),
                stock.getQuantity(),
                new ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse(
                        stock.getColor().getId(),
                        stock.getColor().getName(),
                        stock.getColor().getHexCode()
                ),
                stock.getProductPhotos().stream().map(
                        productPhoto -> new ProductAdminResponse.ProductStockAdminResponse.ProductImageAdminResponse(productPhoto.getId(), productPhoto.getImageUrl(), productPhoto.getAlt())
                ).collect(Collectors.toSet()),
                stock.getInstanceProperties().stream().map(instanceProperty ->
                        new ProductAdminResponse.ProductStockAdminResponse.InstancePropertyDto(
                                instanceProperty.getId(),
                                instanceProperty.getName()
                        )
                ).collect(Collectors.toSet()),
                stock.getPrice())
        ).collect(Collectors.toSet());

        Set<ProductAdminResponse.FeatureAdminResponse> featureDtos = product.getFeatures().stream().map(feature ->
                new ProductAdminResponse.FeatureAdminResponse(
                        feature.getId(),
                        feature.getName(),
                        feature.getDescription(),
                        feature.getImage()
                )
        ).collect(Collectors.toSet());

        return new ProductAdminResponse(product.getId(), product.getName(), product.getDescription(), product.getCreatedAt(), createdByDto, product.getUpdatedAt(), updatedByDto, featureDtos, categoryDto, stockDto);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public CreateProductRequest convertRequestDataToCreateProductRequest(String requestData, Map<String, MultipartFile> files) {
        ObjectMapper objectMapper = new ObjectMapper();
        CreateProductRequest productRequest;
        try {
            productRequest = objectMapper.readValue(requestData, CreateProductRequest.class);

            // Validate the request
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(productRequest);
            if (!violations.isEmpty()) {
                List<ValidationErrorDetail> validationErrors = new ArrayList<>();
                for (ConstraintViolation<CreateProductRequest> violation : violations) {
                    validationErrors.add(new ValidationErrorDetail(
                            violation.getPropertyPath().toString(),
                            violation.getMessage()
                    ));
                }
                throw new ValidationException("Lỗi dữ liệu sản phẩm", validationErrors);
            }
        } catch (JsonProcessingException e) {
            log.info(e.getMessage());
            throw new BadRequestException("Lỗi khi chuyển đổi dữ liệu sản phẩm.");
        }

        // Xử lý ảnh category
        if (productRequest.getCategory().getImage() != null &&
                productRequest.getCategory().getImage().toString().startsWith("placeholder_")) {
            String placeholderKey = productRequest.getCategory().getImage().toString();
            if (files.containsKey(placeholderKey)) {
                productRequest.setCategory(
                        new CreateProductRequest.CreateProductCategoryRequest(
                                productRequest.getCategory().getId(),
                                productRequest.getCategory().getName(),
                                files.get(placeholderKey)
                        )
                );
            }
        }

        // Xử lý ảnh features
        Set<CreateProductRequest.CreateProductFeatureRequest> updatedFeatures = new HashSet<>();
        for (CreateProductRequest.CreateProductFeatureRequest featureRequest : productRequest.getFeatures()) {
            if (featureRequest.getImage() != null &&
                    featureRequest.getImage().toString().startsWith("placeholder_")) {
                String placeholderKey = featureRequest.getImage().toString();
                if (files.containsKey(placeholderKey)) {
                    updatedFeatures.add(
                            new CreateProductRequest.CreateProductFeatureRequest(
                                    featureRequest.getId(),
                                    featureRequest.getName(),
                                    featureRequest.getDescription(),
                                    files.get(placeholderKey)
                            )
                    );
                } else {
                    updatedFeatures.add(featureRequest);
                }
            } else {
                updatedFeatures.add(featureRequest);
            }
        }
        productRequest.setFeatures(updatedFeatures);

        // Xử lý ảnh product photos
        for (CreateProductRequest.CreateProductStockRequest stockRequest : productRequest.getStocks()) {
            Set<CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest> updatedPhotos = new HashSet<>();
            for (CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest photoRequest : stockRequest.getProductPhotos()) {
                if (photoRequest.getImageUrl() != null &&
                        photoRequest.getImageUrl().toString().startsWith("placeholder_")) {
                    String placeholderKey = photoRequest.getImageUrl().toString();
                    if (files.containsKey(placeholderKey)) {
                        updatedPhotos.add(
                                new CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest(
                                        files.get(placeholderKey),
                                        photoRequest.getAlt()
                                )
                        );
                    } else {
                        updatedPhotos.add(photoRequest);
                    }
                } else {
                    updatedPhotos.add(photoRequest);
                }
            }
            stockRequest.setProductPhotos(updatedPhotos);
        }

        return productRequest;
    }
}
