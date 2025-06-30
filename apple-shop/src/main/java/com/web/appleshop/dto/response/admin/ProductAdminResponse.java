package com.web.appleshop.dto.response.admin;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.Product}
 */
@Value
@Builder
@AllArgsConstructor
public class ProductAdminResponse implements Serializable {
    Integer id;
    String name;
    String description;
    LocalDateTime createdAt;
    ProductOwnerAdminResponse createdBy;
    LocalDateTime updatedAt;
    ProductUpdatedAdminResponse updatedBy;
    Set<FeatureAdminResponse> features;
    ProductCategoryAdminResponse category;
    Set<ProductStockAdminResponse> stocks;

    /**
     * DTO for {@link com.web.appleshop.entity.User}
     */
    @Value
    public static class ProductOwnerAdminResponse implements Serializable {
        Integer id;
        String email;
        String firstName;
        String lastName;
        String image;
    }

    /**
     * DTO for {@link com.web.appleshop.entity.User}
     */
    @Value
    public static class ProductUpdatedAdminResponse implements Serializable {
        Integer id;
        String email;
        String firstName;
        String lastName;
        String image;
    }

    /**
     * DTO for {@link com.web.appleshop.entity.Category}
     */
    @Value
    public static class ProductCategoryAdminResponse implements Serializable {
        Integer id;
        String name;
        String image;
    }

    /**
     * DTO for {@link com.web.appleshop.entity.Stock}
     */
    @Value
    public static class ProductStockAdminResponse implements Serializable {
        Integer id;
        Integer quantity;
        ColorAdminResponse color;
        Set<ProductImageAdminResponse> productPhotos;
        Set<InstancePropertyDto> instanceProperties;
        BigDecimal price;

        /**
         * DTO for {@link com.web.appleshop.entity.ProductPhoto}
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ProductImageAdminResponse implements Serializable {
            Integer id;
            String imageUrl;
            String alt;
        }

        /**
         * DTO for {@link com.web.appleshop.entity.Color}
         */
        @Value
        public static class ColorAdminResponse implements Serializable {
            Integer id;
            String name;
            String hexCode;
        }

        /**
         * DTO for {@link com.web.appleshop.entity.InstanceProperty}
         */
        @Value
        public static class InstancePropertyDto implements Serializable {
            Integer id;
            String name;
        }
    }

    /**
     * DTO for {@link com.web.appleshop.entity.Feature}
     */
    @Value
    public static class FeatureAdminResponse implements Serializable {
        Integer id;
        String name;
        String description;
        String image;
    }
}