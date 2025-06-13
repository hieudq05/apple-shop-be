package com.web.appleshop.integration;

import com.web.appleshop.controller.AdminProductController;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Product Unit Tests")
class AdminProductIntegrationTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private AdminProductController adminProductController;

    private ProductAdminResponse sampleProduct;
    private List<ProductAdminResponse> sampleProducts;

    @BeforeEach
    void setUp() {
        // Create sample data for testing
        ProductAdminResponse.ProductOwnerAdminResponse createdBy =
            new ProductAdminResponse.ProductOwnerAdminResponse(
                1, "admin@test.com", "Test", "Admin", "test.jpg", "testadmin"
            );

        ProductAdminResponse.ProductUpdatedAdminResponse updatedBy =
            new ProductAdminResponse.ProductUpdatedAdminResponse(
                1, "admin@test.com", "Test", "Admin", "test.jpg", "testadmin"
            );

        ProductAdminResponse.ProductCategoryAdminResponse category =
            new ProductAdminResponse.ProductCategoryAdminResponse(
                1, "Test Electronics", "test-category.jpg"
            );

        ProductAdminResponse.ProductStockAdminResponse stock =
            new ProductAdminResponse.ProductStockAdminResponse(
                1, 50, new BigDecimal("999.99")
            );

        sampleProduct = new ProductAdminResponse(
            1,
            "Test iPhone",
            "Test iPhone description",
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
    @DisplayName("Should return products with default pagination")
    void getAllProducts_WithDefaultPagination() {
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
        assertThat(response.getBody().getData().getFirst().getName()).isEqualTo("Test iPhone");
        assertThat(response.getBody().getData().getFirst().getDescription()).isEqualTo("Test iPhone description");
        assertThat(response.getBody().getData().getFirst().getCreatedBy().getUsername()).isEqualTo("testadmin");
        assertThat(response.getBody().getData().getFirst().getUpdatedBy().getUsername()).isEqualTo("testadmin");
        assertThat(response.getBody().getData().getFirst().getCategory().getName()).isEqualTo("Test Electronics");
        assertThat(response.getBody().getData().getFirst().getStocks()).hasSize(1);
        assertThat(response.getBody().getMeta()).isNotNull();
    }

    @Test
    @DisplayName("Should return products with custom pagination")
    void getAllProducts_WithCustomPagination() {
        // Given
        List<ProductAdminResponse> multipleProducts = Arrays.asList(
            sampleProduct, sampleProduct, sampleProduct, sampleProduct, sampleProduct
        );
        Pageable expectedPageable = PageRequest.of(0, 5);
        Page<ProductAdminResponse> mockPage = new PageImpl<>(multipleProducts, expectedPageable, 15);
        when(productService.getAllProductsForAdmin(any(Pageable.class))).thenReturn(mockPage);

        // When
        ResponseEntity<ApiResponse<List<ProductAdminResponse>>> response =
            adminProductController.getAllProducts(0, 5);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).hasSize(5);
        assertThat(response.getBody().getMeta()).isNotNull();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void getAllProducts_WithNoProducts() {
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
    @DisplayName("Should handle products with multiple stocks")
    void getAllProducts_WithMultipleStocks() {
        // Given
        ProductAdminResponse.ProductStockAdminResponse stock1 =
            new ProductAdminResponse.ProductStockAdminResponse(1, 50, new BigDecimal("999.99"));
        ProductAdminResponse.ProductStockAdminResponse stock2 =
            new ProductAdminResponse.ProductStockAdminResponse(2, 25, new BigDecimal("1299.99"));

        ProductAdminResponse productWithMultipleStocks = new ProductAdminResponse(
            sampleProduct.getId(),
            sampleProduct.getName(),
            sampleProduct.getDescription(),
            sampleProduct.getCreatedAt(),
            sampleProduct.getCreatedBy(),
            sampleProduct.getUpdatedAt(),
            sampleProduct.getUpdatedBy(),
            sampleProduct.getCategory(),
            Set.of(stock1, stock2)
        );

        Page<ProductAdminResponse> mockPage = new PageImpl<>(List.of(productWithMultipleStocks), PageRequest.of(0, 6), 1);
        when(productService.getAllProductsForAdmin(any(Pageable.class))).thenReturn(mockPage);

        // When
        ResponseEntity<ApiResponse<List<ProductAdminResponse>>> response =
            adminProductController.getAllProducts(null, null);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData().getFirst().getStocks()).hasSize(2);
    }

    @Test
    @DisplayName("Should return correct response structure")
    void getAllProducts_ResponseStructure() {
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
        assertThat(response.getBody().getData().getFirst().getId()).isNotNull();
        assertThat(response.getBody().getData().getFirst().getName()).isNotNull();
        assertThat(response.getBody().getData().getFirst().getDescription()).isNotNull();
        assertThat(response.getBody().getData().getFirst().getCreatedAt()).isNotNull();
        assertThat(response.getBody().getData().getFirst().getUpdatedAt()).isNotNull();
        assertThat(response.getBody().getData().getFirst().getCreatedBy()).isNotNull();
        assertThat(response.getBody().getData().getFirst().getUpdatedBy()).isNotNull();
        assertThat(response.getBody().getData().getFirst().getCategory()).isNotNull();
        assertThat(response.getBody().getData().getFirst().getStocks()).isNotNull();
        assertThat(response.getBody().getMeta()).isNotNull();
    }

    @Test
    @DisplayName("Should handle pagination beyond available data")
    void getAllProducts_PaginationBeyondData() {
        // Given
        Page<ProductAdminResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(10, 5), 0);
        when(productService.getAllProductsForAdmin(any(Pageable.class))).thenReturn(emptyPage);

        // When
        ResponseEntity<ApiResponse<List<ProductAdminResponse>>> response =
            adminProductController.getAllProducts(10, 5);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEmpty();
    }

    @Test
    @DisplayName("Should handle edge case pagination parameters")
    void getAllProducts_EdgeCasePagination() {
        // Given
        Page<ProductAdminResponse> mockPage = new PageImpl<>(Collections.singletonList(sampleProduct), PageRequest.of(0, 1), 1);
        when(productService.getAllProductsForAdmin(any(Pageable.class))).thenReturn(mockPage);

        // When
        ResponseEntity<ApiResponse<List<ProductAdminResponse>>> response =
            adminProductController.getAllProducts(0, 1);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).hasSize(1);
    }
}
