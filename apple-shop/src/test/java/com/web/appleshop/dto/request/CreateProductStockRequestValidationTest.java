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

@DisplayName("CreateProductStockRequest Validation Tests")
class CreateProductStockRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Quantity Validation")
    class QuantityValidation {

        @Test
        @DisplayName("Should pass validation with valid quantity")
        void shouldPassWithValidQuantity() {
            CreateProductRequest.CreateProductStockRequest request = createValidStockRequest();
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest>> violations = 
                    validator.validate(request);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when quantity is null")
        void shouldFailWhenQuantityIsNull() {
            CreateProductRequest.CreateProductStockRequest request = createStockRequestBuilder()
                    .quantity(null)
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest>> violations = 
                    validator.validate(request);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Không được bỏ trống số lượng sản phẩm.");
        }

        @Test
        @DisplayName("Should fail validation when quantity is negative")
        void shouldFailWhenQuantityIsNegative() {
            CreateProductRequest.CreateProductStockRequest request = createStockRequestBuilder()
                    .quantity(-1)
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest>> violations = 
                    validator.validate(request);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Số lượng sản phẩm phải lớn hơn hoặc bằng 0.");
        }

        @Test
        @DisplayName("Should fail validation when quantity exceeds maximum")
        void shouldFailWhenQuantityExceedsMaximum() {
            CreateProductRequest.CreateProductStockRequest request = createStockRequestBuilder()
                    .quantity(1000000)
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest>> violations = 
                    validator.validate(request);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Số lượng sản phẩm không được vượt quá 999,999.");
        }

        @Test
        @DisplayName("Should pass validation with zero quantity")
        void shouldPassWithZeroQuantity() {
            CreateProductRequest.CreateProductStockRequest request = createStockRequestBuilder()
                    .quantity(0)
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest>> violations = 
                    validator.validate(request);
            
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Price Validation")
    class PriceValidation {

        @Test
        @DisplayName("Should fail validation when price is null")
        void shouldFailWhenPriceIsNull() {
            CreateProductRequest.CreateProductStockRequest request = createStockRequestBuilder()
                    .price(null)
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest>> violations = 
                    validator.validate(request);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Không được bỏ trống giá sản phẩm.");
        }

        @Test
        @DisplayName("Should fail validation when price is zero")
        void shouldFailWhenPriceIsZero() {
            CreateProductRequest.CreateProductStockRequest request = createStockRequestBuilder()
                    .price(BigDecimal.ZERO)
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest>> violations = 
                    validator.validate(request);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Giá sản phẩm phải lớn hơn 0.");
        }

        @Test
        @DisplayName("Should fail validation when price is negative")
        void shouldFailWhenPriceIsNegative() {
            CreateProductRequest.CreateProductStockRequest request = createStockRequestBuilder()
                    .price(new BigDecimal("-10.00"))
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest>> violations = 
                    validator.validate(request);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Giá sản phẩm phải lớn hơn 0.");
        }

        @Test
        @DisplayName("Should fail validation when price exceeds maximum")
        void shouldFailWhenPriceExceedsMaximum() {
            CreateProductRequest.CreateProductStockRequest request = createStockRequestBuilder()
                    .price(new BigDecimal("1000000000.00"))
                    .build();

            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest>> violations =
                    validator.validate(request);

            assertThat(violations).hasSize(2);
            assertThat(violations.stream().map(ConstraintViolation::getMessage))
                    .containsExactlyInAnyOrder(
                            "Giá sản phẩm không được vượt quá 999,999,999.99.",
                            "Giá sản phẩm không hợp lệ (tối đa 9 chữ số nguyên và 2 chữ số thập phân)."
                    );
        }

        @Test
        @DisplayName("Should fail validation when price has too many decimal places")
        void shouldFailWhenPriceHasTooManyDecimalPlaces() {
            CreateProductRequest.CreateProductStockRequest request = createStockRequestBuilder()
                    .price(new BigDecimal("99.999"))
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest>> violations = 
                    validator.validate(request);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Giá sản phẩm không hợp lệ (tối đa 9 chữ số nguyên và 2 chữ số thập phân).");
        }

        @Test
        @DisplayName("Should fail validation when price has too many integer digits")
        void shouldFailWhenPriceHasTooManyIntegerDigits() {
            CreateProductRequest.CreateProductStockRequest request = createStockRequestBuilder()
                    .price(new BigDecimal("1234567890.99"))
                    .build();

            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest>> violations =
                    validator.validate(request);

            assertThat(violations).hasSize(2);
            assertThat(violations.stream().map(ConstraintViolation::getMessage))
                    .containsExactlyInAnyOrder(
                            "Giá sản phẩm không được vượt quá 999,999,999.99.",
                            "Giá sản phẩm không hợp lệ (tối đa 9 chữ số nguyên và 2 chữ số thập phân)."
                    );
        }

        @Test
        @DisplayName("Should pass validation with valid price")
        void shouldPassWithValidPrice() {
            CreateProductRequest.CreateProductStockRequest request = createStockRequestBuilder()
                    .price(new BigDecimal("999.99"))
                    .build();
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest>> violations = 
                    validator.validate(request);
            
            assertThat(violations).isEmpty();
        }
    }

    // Helper methods
    private CreateProductRequest.CreateProductStockRequest createValidStockRequest() {
        return createStockRequestBuilder().build();
    }

    private CreateProductStockRequestBuilder createStockRequestBuilder() {
        return new CreateProductStockRequestBuilder()
                .color(createValidColor())
                .quantity(100)
                .price(new BigDecimal("999.99"))
                .productPhotos(Set.of(createValidPhoto()))
                .instanceProperty(Set.of(createValidInstance()));
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
    private static class CreateProductStockRequestBuilder {
        private CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest color;
        private Integer quantity = 100;
        private BigDecimal price = new BigDecimal("999.99");
        private Set<CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest> productPhotos;
        private Set<CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest> instanceProperty;

        public CreateProductStockRequestBuilder color(CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest color) {
            this.color = color;
            return this;
        }

        public CreateProductStockRequestBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public CreateProductStockRequestBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public CreateProductStockRequestBuilder productPhotos(Set<CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest> productPhotos) {
            this.productPhotos = productPhotos;
            return this;
        }

        public CreateProductStockRequestBuilder instanceProperty(Set<CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest> instanceProperties) {
            return this;
        }

        public CreateProductRequest.CreateProductStockRequest build() {
            return new CreateProductRequest.CreateProductStockRequest(color, quantity, price, productPhotos, instanceProperty);
        }
    }
}
