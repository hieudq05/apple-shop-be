package com.web.appleshop.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateProduct Nested Objects Validation Tests")
class CreateProductNestedObjectsValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Color Request Validation")
    class ColorRequestValidation {

        @Test
        @DisplayName("Should pass validation with valid color")
        void shouldPassWithValidColor() {
            CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest color = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest(
                            1, "Space Black", "#000000"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest>> violations = 
                    validator.validate(color);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when color name exceeds 50 characters")
        void shouldFailWhenColorNameExceeds50Characters() {
            String longName = "a".repeat(51);
            CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest color = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest(
                            1, longName, "#000000"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest>> violations = 
                    validator.validate(color);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Tên màu sắc không được vượt quá 50 ký tự.");
        }

        @Test
        @DisplayName("Should fail validation with invalid hex code format")
        void shouldFailWithInvalidHexCodeFormat() {
            CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest color = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest(
                            1, "Red", "invalid-hex"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest>> violations = 
                    validator.validate(color);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Mã màu hex không hợp lệ. Định dạng: #RRGGBB hoặc #RGB");
        }

        @Test
        @DisplayName("Should pass validation with 3-digit hex code")
        void shouldPassWith3DigitHexCode() {
            CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest color = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest(
                            1, "Red", "#F00"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest>> violations = 
                    validator.validate(color);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with 6-digit hex code")
        void shouldPassWith6DigitHexCode() {
            CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest color = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest(
                            1, "Red", "#FF0000"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest>> violations = 
                    validator.validate(color);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation with hex code without hash")
        void shouldFailWithHexCodeWithoutHash() {
            CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest color = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest(
                            1, "Red", "FF0000"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductColorRequest>> violations = 
                    validator.validate(color);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Mã màu hex không hợp lệ. Định dạng: #RRGGBB hoặc #RGB");
        }
    }

    @Nested
    @DisplayName("Photo Request Validation")
    class PhotoRequestValidation {

        @Test
        @DisplayName("Should pass validation with valid photo")
        void shouldPassWithValidPhoto() {
            CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest photo = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest(
                            "https://example.com/photo.jpg", "Product photo"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest>> violations = 
                    validator.validate(photo);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when image URL is blank")
        void shouldFailWhenImageUrlIsBlank() {
            CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest photo = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest(
                            "", "Product photo"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest>> violations = 
                    validator.validate(photo);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("URL ảnh không được bỏ trống.");
        }

        @Test
        @DisplayName("Should fail validation with invalid URL format")
        void shouldFailWithInvalidUrlFormat() {
            CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest photo = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest(
                            "not-a-valid-url", "Product photo"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest>> violations = 
                    validator.validate(photo);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("URL ảnh không hợp lệ.");
        }

        @Test
        @DisplayName("Should fail validation when alt text exceeds 255 characters")
        void shouldFailWhenAltTextExceeds255Characters() {
            String longAlt = "a".repeat(256);
            CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest photo = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest(
                            "https://example.com/photo.jpg", longAlt
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest>> violations = 
                    validator.validate(photo);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Mô tả ảnh không được vượt quá 255 ký tự.");
        }

        @Test
        @DisplayName("Should pass validation with null alt text")
        void shouldPassWithNullAltText() {
            CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest photo = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest(
                            "https://example.com/photo.jpg", null
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductPhotoRequest>> violations = 
                    validator.validate(photo);
            
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Instance Property Request Validation")
    class InstancePropertyRequestValidation {

        @Test
        @DisplayName("Should pass validation with valid instance property")
        void shouldPassWithValidInstanceProperty() {
            CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest instance = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest(
                            1, "128GB"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest>> violations = 
                    validator.validate(instance);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when name exceeds 255 characters")
        void shouldFailWhenNameExceeds255Characters() {
            String longName = "a".repeat(256);
            CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest instance = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest(
                            1, longName
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest>> violations = 
                    validator.validate(instance);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Tên thuộc tính không được vượt quá 255 ký tự.");
        }

        @Test
        @DisplayName("Should pass validation with null name")
        void shouldPassWithNullName() {
            CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest instance = 
                    new CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest(
                            1, null
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductStockRequest.CreateProductInstanceRequest>> violations = 
                    validator.validate(instance);
            
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Feature Request Validation")
    class FeatureRequestValidation {

        @Test
        @DisplayName("Should pass validation with valid feature")
        void shouldPassWithValidFeature() {
            CreateProductRequest.CreateProductFeatureRequest feature = 
                    new CreateProductRequest.CreateProductFeatureRequest(
                            1, "Face ID", "Advanced facial recognition", "https://example.com/feature.jpg"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductFeatureRequest>> violations = 
                    validator.validate(feature);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when feature name exceeds 100 characters")
        void shouldFailWhenFeatureNameExceeds100Characters() {
            String longName = "a".repeat(101);
            CreateProductRequest.CreateProductFeatureRequest feature = 
                    new CreateProductRequest.CreateProductFeatureRequest(
                            1, longName, "Description", "https://example.com/feature.jpg"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductFeatureRequest>> violations = 
                    validator.validate(feature);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Tên tính năng không được vượt quá 100 ký tự.");
        }

        @Test
        @DisplayName("Should fail validation when feature description exceeds 500 characters")
        void shouldFailWhenFeatureDescriptionExceeds500Characters() {
            String longDescription = "a".repeat(501);
            CreateProductRequest.CreateProductFeatureRequest feature = 
                    new CreateProductRequest.CreateProductFeatureRequest(
                            1, "Feature", longDescription, "https://example.com/feature.jpg"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductFeatureRequest>> violations = 
                    validator.validate(feature);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Mô tả tính năng không được vượt quá 500 ký tự.");
        }

        @Test
        @DisplayName("Should fail validation with invalid image URL")
        void shouldFailWithInvalidImageUrl() {
            CreateProductRequest.CreateProductFeatureRequest feature = 
                    new CreateProductRequest.CreateProductFeatureRequest(
                            1, "Feature", "Description", "invalid-url"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductFeatureRequest>> violations = 
                    validator.validate(feature);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("URL ảnh tính năng không hợp lệ.");
        }
    }

    @Nested
    @DisplayName("Category Request Validation")
    class CategoryRequestValidation {

        @Test
        @DisplayName("Should pass validation with valid category")
        void shouldPassWithValidCategory() {
            CreateProductRequest.CreateProductCategoryRequest category = 
                    new CreateProductRequest.CreateProductCategoryRequest(
                            1, "Smartphones", "https://example.com/category.jpg"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductCategoryRequest>> violations = 
                    validator.validate(category);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when category name exceeds 255 characters")
        void shouldFailWhenCategoryNameExceeds255Characters() {
            String longName = "a".repeat(256);
            CreateProductRequest.CreateProductCategoryRequest category = 
                    new CreateProductRequest.CreateProductCategoryRequest(
                            1, longName, "https://example.com/category.jpg"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductCategoryRequest>> violations = 
                    validator.validate(category);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("Tên danh mục không được vượt quá 255 ký tự.");
        }

        @Test
        @DisplayName("Should fail validation with invalid image URL")
        void shouldFailWithInvalidImageUrl() {
            CreateProductRequest.CreateProductCategoryRequest category = 
                    new CreateProductRequest.CreateProductCategoryRequest(
                            1, "Category", "invalid-url"
                    );
            
            Set<ConstraintViolation<CreateProductRequest.CreateProductCategoryRequest>> violations = 
                    validator.validate(category);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("URL ảnh danh mục không hợp lệ.");
        }
    }
}
