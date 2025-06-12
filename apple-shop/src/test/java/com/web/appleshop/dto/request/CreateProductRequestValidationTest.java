package com.web.appleshop.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateProductRequest Validation Tests")
class CreateProductRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Product Name Validation")
    class ProductNameValidation {

        @Test
        @DisplayName("Should pass validation with valid product name")
        void shouldPassWithValidProductName() {
            CreateProductRequest request = createValidRequest();
            
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when product name is blank")
        void shouldFailWhenProductNameIsBlank() {
            CreateProductRequest request = createRequestBuilder()
                    .name("")
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Không được bỏ trống tên sản phẩm.");
        }

        @Test
        @DisplayName("Should fail validation when product name exceeds 255 characters")
        void shouldFailWhenProductNameExceeds255Characters() {
            String longName = "a".repeat(256);
            CreateProductRequest request = createRequestBuilder()
                    .name(longName)
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Tên sản phẩm không được vượt quá 255 ký tự.");
        }
    }

    @Nested
    @DisplayName("Product Description Validation")
    class ProductDescriptionValidation {

        @Test
        @DisplayName("Should fail validation when description is blank")
        void shouldFailWhenDescriptionIsBlank() {
            CreateProductRequest request = createRequestBuilder()
                    .description("")
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Không được bỏ trống mô tả sản phẩm.");
        }

        @Test
        @DisplayName("Should fail validation when description exceeds 5000 characters")
        void shouldFailWhenDescriptionExceeds5000Characters() {
            String longDescription = "a".repeat(5001);
            CreateProductRequest request = createRequestBuilder()
                    .description(longDescription)
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Mô tả sản phẩm không được vượt quá 5000 ký tự.");
        }
    }

    @Nested
    @DisplayName("Created By Validation")
    class CreatedByValidation {

        @Test
        @DisplayName("Should fail validation when createdBy is blank")
        void shouldFailWhenCreatedByIsBlank() {
            CreateProductRequest request = createRequestBuilder()
                    .createdBy("")
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Không được bỏ trống người tạo sản phẩm.");
        }

        @Test
        @DisplayName("Should fail validation when createdBy exceeds 255 characters")
        void shouldFailWhenCreatedByExceeds255Characters() {
            String longCreatedBy = "a".repeat(256);
            CreateProductRequest request = createRequestBuilder()
                    .createdBy(longCreatedBy)
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Tên người tạo không được vượt quá 255 ký tự.");
        }
    }

    @Nested
    @DisplayName("Collections Validation")
    class CollectionsValidation {

        @Test
        @DisplayName("Should fail validation when features set is null")
        void shouldFailWhenFeaturesIsNull() {
            CreateProductRequest request = createRequestBuilder()
                    .features(null)
                    .build();

            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(2);
            assertThat(violations.stream().map(ConstraintViolation::getMessage))
                    .containsExactlyInAnyOrder(
                            "Không được bỏ trống tính năng sản phẩm.",
                            "Danh sách tính năng không được rỗng."
                    );
        }

        @Test
        @DisplayName("Should fail validation when features set is empty")
        void shouldFailWhenFeaturesIsEmpty() {
            CreateProductRequest request = createRequestBuilder()
                    .features(Set.of())
                    .build();

            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Danh sách tính năng không được rỗng.");
        }

        @Test
        @DisplayName("Should fail validation when stocks set is null")
        void shouldFailWhenStocksIsNull() {
            CreateProductRequest request = createRequestBuilder()
                    .stocks(null)
                    .build();

            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(2);
            assertThat(violations.stream().map(ConstraintViolation::getMessage))
                    .containsExactlyInAnyOrder(
                            "Không được bỏ trống kho sản phẩm.",
                            "Danh sách kho sản phẩm không được rỗng."
                    );
        }

        @Test
        @DisplayName("Should fail validation when stocks set is empty")
        void shouldFailWhenStocksIsEmpty() {
            CreateProductRequest request = createRequestBuilder()
                    .stocks(Set.of())
                    .build();

            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Danh sách kho sản phẩm không được rỗng.");
        }
    }

    // Helper methods
    private CreateProductRequest createValidRequest() {
        return createRequestBuilder().build();
    }

    private CreateProductRequestBuilder createRequestBuilder() {
        return new CreateProductRequestBuilder()
                .name("iPhone 15 Pro")
                .description("Latest iPhone with advanced features")
                .createdBy("admin")
                .category(createValidCategory())
                .features(Set.of(createValidFeature()))
                .stocks(Set.of(createValidStock()));
    }

    private CreateProductRequest.CreateProductCategoryRequest createValidCategory() {
        return new CreateProductRequest.CreateProductCategoryRequest(
                1, "Smartphones", "https://example.com/category.jpg"
        );
    }

    private CreateProductRequest.CreateProductFeatureRequest createValidFeature() {
        return new CreateProductRequest.CreateProductFeatureRequest(
                1, "Face ID", "Advanced facial recognition", "https://example.com/feature.jpg"
        );
    }

    private CreateProductRequest.CreateProductStockRequest createValidStock() {
        return new CreateProductRequest.CreateProductStockRequest(
                createValidColor(),
                100,
                new BigDecimal("999.99"),
                Set.of(createValidPhoto()),
                Set.of(createValidInstance())
        );
    }

    private CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest createValidColor() {
        return new CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest(
                1, "Space Black", "#000000"
        );
    }

    private CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest createValidPhoto() {
        return new CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest(
                "https://example.com/photo.jpg", "iPhone photo"
        );
    }

    private CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest createValidInstance() {
        return new CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest(
                1, "128GB"
        );
    }

    // Builder class for easier test data creation
    private static class CreateProductRequestBuilder {
        private String name = "Test Product";
        private String description = "Test Description";
        private String createdBy = "test-user";
        private CreateProductRequest.CreateProductCategoryRequest category;
        private Set<CreateProductRequest.CreateProductFeatureRequest> features;
        private Set<CreateProductRequest.CreateProductStockRequest> stocks;

        public CreateProductRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CreateProductRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CreateProductRequestBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public CreateProductRequestBuilder category(CreateProductRequest.CreateProductCategoryRequest category) {
            this.category = category;
            return this;
        }

        public CreateProductRequestBuilder features(Set<CreateProductRequest.CreateProductFeatureRequest> features) {
            this.features = features;
            return this;
        }

        public CreateProductRequestBuilder stocks(Set<CreateProductRequest.CreateProductStockRequest> stocks) {
            this.stocks = stocks;
            return this;
        }

        public CreateProductRequest build() {
            return new CreateProductRequest(name, description, createdBy, category, features, stocks);
        }
    }
}
