package com.web.appleshop.dto.request;

import com.web.appleshop.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.Product}
 */
@Value
public class CreateProductRequest implements Serializable {
    @NotBlank(message = "Không được bỏ trống tên sản phẩm.")
    @Length(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự.")
    String name;

    @NotBlank(message = "Không được bỏ trống mô tả sản phẩm.")
    String description;

    @NotBlank(message = "Không được bỏ trống người tạo sản phẩm.")
    String createdBy;

    @NotNull(message = "Không được bỏ trống danh mục sản phẩm.")
    CreateProductRequest.CreateProductCategoryRequest category;

    @NotNull(message = "Không được bỏ trống tính năng sản phẩm.")
    Set<CreateProductRequest.CreateProductFeatureRequest> features;

    @NotNull(message = "Không được bỏ trống kho sản phẩm.")
    Set<CreateProductRequest.CreateProductStockRequest> stocks;

    @Value
    public static class CreateProductStockRequest implements Serializable {
        @NotNull(message = "Không được bỏ trống màu sắc.")
        CreateProductStockRequest.CreateProductColorRequest color;
        Integer quantity;
        BigDecimal price;
        @NotNull(message = "Không được bỏ trống ảnh sản phẩm.")
        Set<CreateProductPhotoRequest> productPhotos;
        @NotNull(message = "Không được bỏ trống thuộc tính kho sản phẩm.")
        Set<CreateProductInstanceRequest> instanceProperties;

        /**
         * DTO for {@link com.web.appleshop.entity.Color}
         */
        @Value
        public static class CreateProductColorRequest implements Serializable {
            Integer id;
            String name;
            String hexCode;
        }

        /**
         * DTO for {@link com.web.appleshop.entity.ProductPhoto}
         */
        @Value
        public static class CreateProductPhotoRequest implements Serializable {
            String imageUrl;
            String alt;
        }

        /**
         * DTO for {@link com.web.appleshop.entity.InstanceProperty}
         */
        @Value
        public static class CreateProductInstanceRequest implements Serializable {
            Integer id;
            String name;
        }
    }

    @Value
    public static class CreateProductFeatureRequest implements Serializable {
        Integer id;
        String name;
        String description;
        String image;
    }

    @Value
    public static class CreateProductCategoryRequest implements Serializable {
        Integer id;
        String name;
        String image;
    }
}