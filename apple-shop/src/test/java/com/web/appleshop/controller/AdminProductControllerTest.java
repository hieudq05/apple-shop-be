package com.web.appleshop.controller;

import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.ProductAdminResponse;
import com.web.appleshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminProductController Tests")
class AdminProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private AdminProductController adminProductController;

    private List<ProductAdminResponse> sampleProducts;

    @BeforeEach
    void setUp() {
        // Create sample data for testing
        ProductAdminResponse.ProductOwnerAdminResponse createdBy = 
            new ProductAdminResponse.ProductOwnerAdminResponse(
                1, "admin@example.com", "John", "Doe", "image.jpg", "admin"
            );

        ProductAdminResponse.ProductUpdatedAdminResponse updatedBy = 
            new ProductAdminResponse.ProductUpdatedAdminResponse(
                1, "admin@example.com", "John", "Doe", "image.jpg", "admin"
            );

        ProductAdminResponse.ProductCategoryAdminResponse category = 
            new ProductAdminResponse.ProductCategoryAdminResponse(
                1, "Electronics", "category.jpg"
            );

        ProductAdminResponse.ProductStockAdminResponse stock = 
            new ProductAdminResponse.ProductStockAdminResponse(
                1, 100, new BigDecimal("999.99")
            );

        ProductAdminResponse sampleProduct = new ProductAdminResponse(
                1,
                "iPhone 15",
                "Latest iPhone model",
                LocalDateTime.now(),
                createdBy,
                LocalDateTime.now(),
                updatedBy,
                category,
                Set.of(stock)
        );

        sampleProducts = List.of(sampleProduct);
    }

    @Test
    @DisplayName("Should return products with default pagination when no parameters provided")
    void getAllProducts_WithoutParameters_ShouldReturnDefaultPagination() {
        // Given
        Pageable expectedPageable = PageRequest.of(0, 6);
        Page<ProductAdminResponse> mockPage = new PageImpl<>(sampleProducts, expectedPageable, 1);
        when(productService.getAllProductsForAdmin(any(Pageable.class))).thenReturn(mockPage);

        // When
        ResponseEntity<ApiResponse<List<ProductAdminResponse>>> response =
            adminProductController.getAllProducts(null, null);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Get all products successfully");
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getData().getFirst().getId()).isEqualTo(1);
        assertThat(response.getBody().getData().getFirst().getName()).isEqualTo("iPhone 15");
        assertThat(response.getBody().getMeta()).isNotNull();
    }

    @Test
    @DisplayName("Should return products with custom pagination when parameters provided")
    void getAllProducts_WithCustomParameters_ShouldReturnCustomPagination() {
        // Given
        Pageable expectedPageable = PageRequest.of(1, 10);
        Page<ProductAdminResponse> mockPage = new PageImpl<>(sampleProducts, expectedPageable, 15);
        when(productService.getAllProductsForAdmin(any(Pageable.class))).thenReturn(mockPage);

        // When
        ResponseEntity<ApiResponse<List<ProductAdminResponse>>> response =
            adminProductController.getAllProducts(1, 10);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Get all products successfully");
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getMeta()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null page parameter correctly")
    void getAllProducts_WithNullPage_ShouldUseDefaultPage() {
        // Given
        Page<ProductAdminResponse> mockPage = new PageImpl<>(sampleProducts, PageRequest.of(0, 5), 1);
        when(productService.getAllProductsForAdmin(any(Pageable.class))).thenReturn(mockPage);

        // When
        ResponseEntity<ApiResponse<List<ProductAdminResponse>>> response =
            adminProductController.getAllProducts(null, 5);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
    }

    @Test
    @DisplayName("Should handle null size parameter correctly")
    void getAllProducts_WithNullSize_ShouldUseDefaultSize() {
        // Given
        Page<ProductAdminResponse> mockPage = new PageImpl<>(sampleProducts, PageRequest.of(2, 6), 20);
        when(productService.getAllProductsForAdmin(any(Pageable.class))).thenReturn(mockPage);

        // When
        ResponseEntity<ApiResponse<List<ProductAdminResponse>>> response =
            adminProductController.getAllProducts(2, null);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void getAllProducts_WithNoProducts_ShouldReturnEmptyList() {
        // Given
        Page<ProductAdminResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 6), 0);
        when(productService.getAllProductsForAdmin(any(Pageable.class))).thenReturn(emptyPage);

        // When
        ResponseEntity<ApiResponse<List<ProductAdminResponse>>> response =
            adminProductController.getAllProducts(null, null);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEmpty();
    }

    @Test
    @DisplayName("Should handle large page numbers correctly")
    void getAllProducts_WithLargePageNumber_ShouldHandleCorrectly() {
        // Given
        Page<ProductAdminResponse> mockPage = new PageImpl<>(List.of(), PageRequest.of(999, 6), 0);
        when(productService.getAllProductsForAdmin(any(Pageable.class))).thenReturn(mockPage);

        // When
        ResponseEntity<ApiResponse<List<ProductAdminResponse>>> response =
            adminProductController.getAllProducts(999, null);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEmpty();
    }

    @Test
    @DisplayName("Should validate response structure contains all required fields")
    void getAllProducts_ShouldReturnCorrectResponseStructure() {
        // Given
        Page<ProductAdminResponse> mockPage = new PageImpl<>(sampleProducts, PageRequest.of(0, 6), 1);
        when(productService.getAllProductsForAdmin(any(Pageable.class))).thenReturn(mockPage);

        // When
        ResponseEntity<ApiResponse<List<ProductAdminResponse>>> response =
            adminProductController.getAllProducts(null, null);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Get all products successfully");
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getData().getFirst().getId()).isEqualTo(1);
        assertThat(response.getBody().getData().getFirst().getName()).isEqualTo("iPhone 15");
        assertThat(response.getBody().getData().getFirst().getDescription()).isEqualTo("Latest iPhone model");
        assertThat(response.getBody().getData().getFirst().getCreatedBy()).isNotNull();
        assertThat(response.getBody().getData().getFirst().getUpdatedBy()).isNotNull();
        assertThat(response.getBody().getData().getFirst().getCategory()).isNotNull();
        assertThat(response.getBody().getData().getFirst().getStocks()).isNotEmpty();
        assertThat(response.getBody().getMeta()).isNotNull();
    }
}
