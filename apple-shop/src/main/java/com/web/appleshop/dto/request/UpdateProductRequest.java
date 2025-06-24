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
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProductRequest implements Serializable {
    @NotBlank(message = "Không được bỏ trống tên sản phẩm.")
    @Length(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự.")
    String name;

    @NotBlank(message = "Không được bỏ trống mô tả sản phẩm.")
    @Length(max = 5000, message = "Mô tả sản phẩm không được vượt quá 5000 ký tự.")
    String description;

    @NotNull(message = "Không được bỏ trống danh mục sản phẩm.")
    @Valid
    CategoryDto category;

    @NotNull(message = "Không được bỏ trống tính năng sản phẩm.")
    @NotEmpty(message = "Danh sách tính năng không được rỗng.")
    @Valid
    Set<FeatureDto> features;

    @NotNull(message = "Không được bỏ trống kho sản phẩm.")
    @NotEmpty(message = "Danh sách kho sản phẩm không được rỗng.")
    @Valid
    Set<StockDto> stocks;

    /**
     * DTO for {@link com.web.appleshop.entity.Category}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDto implements Serializable {
        Integer id;

        @NotBlank(message = "Không được bỏ trống tên danh mục.")
        @Length(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự.")
        String name;

        @NotNull(message = "Không được bỏ trống ảnh danh mục.")
        Object image;
    }

    /**
     * DTO for {@link com.web.appleshop.entity.Feature}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FeatureDto implements Serializable {
        Integer id;

        @NotBlank(message = "Không được bỏ trống tên tính năng.")
        @Length(max = 100, message = "Tên tính năng không được vượt quá 100 ký tự.")
        String name;

        @NotBlank(message = "Không được bỏ trống mô tả tính năng.")
        @Length(max = 500, message = "Mô tả tính năng không được vượt quá 500 ký tự.")
        String description;

        @NotNull(message = "Không được bỏ trống ảnh tính năng.")
        Object image;
    }

    /**
     * DTO for {@link com.web.appleshop.entity.Stock}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StockDto implements Serializable {
        Integer id;

        @NotNull(message = "Không được bỏ trống màu sắc.")
        @Valid
        ColorDto color;

        @NotNull(message = "Không được bỏ trống số lượng sản phẩm.")
        @Min(value = 0, message = "Số lượng sản phẩm phải lớn hơn hoặc bằng 0.")
        @Max(value = 999999, message = "Số lượng sản phẩm không được vượt quá 999,999.")
        Integer quantity;

        @NotNull(message = "Không được bỏ trống giá sản phẩm.")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá sản phẩm phải lớn hơn 0.")
        @DecimalMax(value = "999999999.99", message = "Giá sản phẩm không được vượt quá 999,999,999.99.")
        BigDecimal price;

        @NotNull(message = "Không được bỏ trống ảnh sản phẩm.")
        @NotEmpty(message = "Danh sách ảnh sản phẩm không được rỗng.")
        @Valid
        Set<ProductPhotoDto> productPhotos;

        @NotNull(message = "Không được bỏ trống thuộc tính kho sản phẩm.")
        @NotEmpty(message = "Danh sách thuộc tính kho sản phẩm không được rỗng.")
        @Valid
        Set<InstancePropertyDto> instanceProperties;

        /**
         * DTO for {@link com.web.appleshop.entity.Color}
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ColorDto implements Serializable {
            Integer id;

            @NotBlank(message = "Không được bỏ trống tên màu sắc.")
            @Length(max = 50, message = "Tên màu sắc không được vượt quá 50 ký tự.")
            String name;

            @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})?$"
                    , message = "Mã màu hex không hợp lệ. Định dạng: #RRGGBB hoặc #RGB")
            String hexCode;
        }

        /**
         * DTO for {@link com.web.appleshop.entity.ProductPhoto}
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ProductPhotoDto implements Serializable {
            Integer id;

            @NotNull(message = "Không được bỏ trống ảnh sản phẩm.")
            Object imageUrl;

            @NotBlank(message = "Không được bỏ trống mô tả ảnh.")
            @Length(max = 255, message = "Mô tả ảnh không được vượt quá 255 ký tự.")
            String alt;
        }

        /**
         * DTO for {@link com.web.appleshop.entity.InstanceProperty}
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class InstancePropertyDto implements Serializable {
            Integer id;

            @NotBlank(message = "Không được bỏ trống tên thuộc tính.")
            @Length(max = 255, message = "Tên thuộc tính không được vượt quá 255 ký tự.")
            String name;
        }
    }
}
