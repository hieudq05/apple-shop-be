package com.web.appleshop.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.Product}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest implements Serializable {
    @NotBlank(message = "Không được bỏ trống tên sản phẩm.")
    @Length(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự.")
    String name;

    @NotBlank(message = "Không được bỏ trống mô tả sản phẩm.")
    @Length(max = 5000, message = "Mô tả sản phẩm không được vượt quá 5000 ký tự.")
    String description;

    @NotBlank(message = "Không được bỏ trống người tạo sản phẩm.")
    @Length(max = 255, message = "Tên người tạo không được vượt quá 255 ký tự.")
    String createdBy;

    @NotNull(message = "Không được bỏ trống danh mục sản phẩm.")
    @Valid
    CreateProductRequest.CreateProductCategoryRequest category;

    @NotNull(message = "Không được bỏ trống tính năng sản phẩm.")
    @NotEmpty(message = "Danh sách tính năng không được rỗng.")
    @Valid
    Set<CreateProductRequest.CreateProductFeatureRequest> features;

    @NotNull(message = "Không được bỏ trống kho sản phẩm.")
    @NotEmpty(message = "Danh sách kho sản phẩm không được rỗng.")
    @Valid
    Set<CreateProductRequest.CreateProductStockRequest> stocks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductStockRequest implements Serializable {

        @NotNull(message = "Không được bỏ trống màu sắc.")
        CreateProductStockRequest.CreateProductColorRequest color;

        @NotNull(message = "Không được bỏ trống số lượng sản phẩm.")
        @Min(value = 0, message = "Số lượng sản phẩm phải lớn hơn hoặc bằng 0.")
        @Max(value = 999999, message = "Số lượng sản phẩm không được vượt quá 999,999.")
        Integer quantity;

        @NotNull(message = "Không được bỏ trống giá sản phẩm.")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá sản phẩm phải lớn hơn 0.")
        @DecimalMax(value = "999999999.99", message = "Giá sản phẩm không được vượt quá 999,999,999.99.")
        @Digits(integer = 9, fraction = 2, message = "Giá sản phẩm không hợp lệ (tối đa 9 chữ số nguyên và 2 chữ số thập phân).")
        BigDecimal price;

        @NotNull(message = "Không được bỏ trống ảnh sản phẩm.")
        @NotEmpty(message = "Danh sách ảnh sản phẩm không được rỗng.")
        @Valid
        Set<CreateProductPhotoRequest> productPhotos;

        @NotNull(message = "Không được bỏ trống thuộc tính kho sản phẩm.")
        @NotEmpty(message = "Danh sách thuộc tính kho sản phẩm không được rỗng.")
        @Valid
        Set<CreateProductInstanceRequest> instanceProperties;

        /**
         * DTO for {@link com.web.appleshop.entity.Color}
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CreateProductColorRequest implements Serializable {
            Integer id;

            @Length(max = 50, message = "Tên màu sắc không được vượt quá 50 ký tự.")
            String name;

            // Hex code can blank
            @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})?$"
                    , message = "Mã màu hex không hợp lệ. Định dạng: #RRGGBB hoặc #RGB")
            String hexCode;
        }

        /**
         * DTO for {@link com.web.appleshop.entity.ProductPhoto}
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CreateProductPhotoRequest implements Serializable {
            @NotNull(message = "URL ảnh không được bỏ trống.")
            Object imageUrl;

            @Length(max = 255, message = "Mô tả ảnh không được vượt quá 255 ký tự.")
            String alt;
        }

        /**
         * DTO for {@link com.web.appleshop.entity.InstanceProperty}
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CreateProductInstanceRequest implements Serializable {
            Integer id;

            @Length(max = 255, message = "Tên thuộc tính không được vượt quá 255 ký tự.")
            String name;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductFeatureRequest implements Serializable {
        Integer id;

        @Length(max = 100, message = "Tên tính năng không được vượt quá 100 ký tự.")
        String name;

        @Length(max = 500, message = "Mô tả tính năng không được vượt quá 500 ký tự.")
        String description;

        @NotNull(message = "Không được bỏ trống ảnh tính năng.")
        Object image;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductCategoryRequest implements Serializable {
        Integer id;

        @Length(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự.")
        String name;

        Object image;
    }
}