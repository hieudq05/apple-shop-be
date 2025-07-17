package com.web.appleshop.dto.response;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.Product}
 */
@Value
public class ProductUserResponse implements Serializable {
    Integer id;
    String name;
    String description;
    Set<ProductStockResponse> stocks;
    Integer categoryId;

    /**
     * DTO for {@link com.web.appleshop.entity.Stock}
     */
    @Value
    public static class ProductStockResponse implements Serializable {
        Integer id;
        StockColorResponse color;
        Integer quantity;
        BigDecimal price;
        Set<StockPhotoResponse> productPhotos;
        Set<StockInstanceResponse> instanceProperty;

        /**
         * DTO for {@link com.web.appleshop.entity.Color}
         */
        @Value
        public static class StockColorResponse implements Serializable {
            Integer id;
            String name;
            String hexCode;
        }

        /**
         * DTO for {@link com.web.appleshop.entity.ProductPhoto}
         */
        @Value
        public static class StockPhotoResponse implements Serializable {
            Integer id;
            String imageUrl;
            String alt;
        }

        /**
         * DTO for {@link com.web.appleshop.entity.InstanceProperty}
         */
        @Value
        public static class StockInstanceResponse implements Serializable {
            Integer id;
            String name;
        }
    }
}