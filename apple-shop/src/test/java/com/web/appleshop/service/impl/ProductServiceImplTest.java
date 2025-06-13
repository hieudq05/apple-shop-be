package com.web.appleshop.service.impl;

import com.web.appleshop.dto.response.ProductAdminResponse;
import com.web.appleshop.entity.Category;
import com.web.appleshop.entity.Product;
import com.web.appleshop.entity.Stock;
import com.web.appleshop.entity.User;
import com.web.appleshop.repository.ProductRepository;
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
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product sampleProduct;
    private Stock sampleStock;

    @BeforeEach
    void setUp() {
        // Create sample entities
        User sampleUser = new User();
        sampleUser.setId(1);
        sampleUser.setEmail("admin@example.com");
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setImage("user.jpg");
        sampleUser.setUsername("admin");

        Category sampleCategory = new Category();
        sampleCategory.setId(1);
        sampleCategory.setName("Electronics");
        sampleCategory.setImage("category.jpg");

        sampleStock = new Stock();
        sampleStock.setId(1);
        sampleStock.setQuantity(100);
        sampleStock.setPrice(new BigDecimal("999.99"));

        sampleProduct = new Product();
        sampleProduct.setId(1);
        sampleProduct.setName("iPhone 15");
        sampleProduct.setDescription("Latest iPhone model");
        sampleProduct.setCreatedAt(LocalDateTime.now());
        sampleProduct.setCreatedBy(sampleUser);
        sampleProduct.setUpdatedAt(LocalDateTime.now());
        sampleProduct.setUpdatedBy(sampleUser);
        sampleProduct.setCategory(sampleCategory);
        
        Set<Stock> stocks = new HashSet<>();
        stocks.add(sampleStock);
        sampleProduct.setStocks(stocks);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    @DisplayName("Should return paginated products for admin")
    void getAllProductsForAdmin_WithValidPageable_ShouldReturnPagedProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Collections.singletonList(sampleProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);
        
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<ProductAdminResponse> result = productService.getAllProductsForAdmin(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getPageable()).isEqualTo(pageable);
        
        ProductAdminResponse productResponse = result.getContent().getFirst();
        assertThat(productResponse.getId()).isEqualTo(1);
        assertThat(productResponse.getName()).isEqualTo("iPhone 15");
        assertThat(productResponse.getDescription()).isEqualTo("Latest iPhone model");
        
        verify(productRepository).findAll(pageable);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_STAFF"})
    @DisplayName("Should allow staff to get products")
    void getAllProductsForAdmin_WithStaffRole_ShouldAllowAccess() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(sampleProduct), pageable, 1);
        
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<ProductAdminResponse> result = productService.getAllProductsForAdmin(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(productRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should return empty page when no products exist")
    void getAllProductsForAdmin_WithNoProducts_ShouldReturnEmptyPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        
        when(productRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        Page<ProductAdminResponse> result = productService.getAllProductsForAdmin(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(productRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should handle large page requests correctly")
    void getAllProductsForAdmin_WithLargePage_ShouldHandleCorrectly() {
        // Given
        Pageable pageable = PageRequest.of(100, 50);
        Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        
        when(productRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        Page<ProductAdminResponse> result = productService.getAllProductsForAdmin(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(productRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should convert Product to ProductAdminResponse correctly")
    void convertProductToProductAdminResponse_WithValidProduct_ShouldConvertCorrectly() {
        // When
        ProductAdminResponse result = productService.convertProductToProductAdminResponse(sampleProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("iPhone 15");
        assertThat(result.getDescription()).isEqualTo("Latest iPhone model");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        
        // Verify createdBy mapping
        assertThat(result.getCreatedBy()).isNotNull();
        assertThat(result.getCreatedBy().getId()).isEqualTo(1);
        assertThat(result.getCreatedBy().getEmail()).isEqualTo("admin@example.com");
        assertThat(result.getCreatedBy().getFirstName()).isEqualTo("John");
        assertThat(result.getCreatedBy().getLastName()).isEqualTo("Doe");
        assertThat(result.getCreatedBy().getUsername()).isEqualTo("admin");
        
        // Verify updatedBy mapping
        assertThat(result.getUpdatedBy()).isNotNull();
        assertThat(result.getUpdatedBy().getId()).isEqualTo(1);
        assertThat(result.getUpdatedBy().getEmail()).isEqualTo("admin@example.com");
        
        // Verify category mapping
        assertThat(result.getCategory()).isNotNull();
        assertThat(result.getCategory().getId()).isEqualTo(1);
        assertThat(result.getCategory().getName()).isEqualTo("Electronics");
        assertThat(result.getCategory().getImage()).isEqualTo("category.jpg");
        
        // Verify stocks mapping
        assertThat(result.getStocks()).isNotNull();
        assertThat(result.getStocks()).hasSize(1);
        ProductAdminResponse.ProductStockAdminResponse stockResponse = result.getStocks().iterator().next();
        assertThat(stockResponse.getId()).isEqualTo(1);
        assertThat(stockResponse.getQuantity()).isEqualTo(100);
        assertThat(stockResponse.getPrice()).isEqualTo(new BigDecimal("999.99"));
    }

    @Test
    @DisplayName("Should handle product with multiple stocks")
    void convertProductToProductAdminResponse_WithMultipleStocks_ShouldConvertAll() {
        // Given
        Stock additionalStock = new Stock();
        additionalStock.setId(2);
        additionalStock.setQuantity(50);
        additionalStock.setPrice(new BigDecimal("1299.99"));
        
        Set<Stock> stocks = new HashSet<>();
        stocks.add(sampleStock);
        stocks.add(additionalStock);
        sampleProduct.setStocks(stocks);

        // When
        ProductAdminResponse result = productService.convertProductToProductAdminResponse(sampleProduct);

        // Then
        assertThat(result.getStocks()).hasSize(2);
        assertThat(result.getStocks().stream()
                .mapToInt(ProductAdminResponse.ProductStockAdminResponse::getId))
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    @DisplayName("Should handle product with empty stocks")
    void convertProductToProductAdminResponse_WithEmptyStocks_ShouldReturnEmptySet() {
        // Given
        sampleProduct.setStocks(new HashSet<>());

        // When
        ProductAdminResponse result = productService.convertProductToProductAdminResponse(sampleProduct);

        // Then
        assertThat(result.getStocks()).isEmpty();
    }

    @Test
    @DisplayName("Should preserve all timestamps during conversion")
    void convertProductToProductAdminResponse_ShouldPreserveTimestamps() {
        // Given
        LocalDateTime specificTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        sampleProduct.setCreatedAt(specificTime);
        sampleProduct.setUpdatedAt(specificTime.plusHours(1));

        // When
        ProductAdminResponse result = productService.convertProductToProductAdminResponse(sampleProduct);

        // Then
        assertThat(result.getCreatedAt()).isEqualTo(specificTime);
        assertThat(result.getUpdatedAt()).isEqualTo(specificTime.plusHours(1));
    }

    @Test
    @DisplayName("Should call repository with correct pageable parameters")
    void getAllProductsForAdmin_ShouldCallRepositoryWithCorrectParameters() {
        // Given
        Pageable customPageable = PageRequest.of(2, 5);
        Page<Product> mockPage = new PageImpl<>(List.of(), customPageable, 0);
        when(productRepository.findAll(customPageable)).thenReturn(mockPage);

        // When
        productService.getAllProductsForAdmin(customPageable);

        // Then
        verify(productRepository).findAll(customPageable);
        verifyNoMoreInteractions(productRepository);
    }
}
