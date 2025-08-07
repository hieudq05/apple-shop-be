package com.web.appleshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.SavedProduct}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSavedProductDto implements Serializable {
    StockDto stock;
    LocalDateTime createdAt;

    /**
     * DTO for {@link com.web.appleshop.entity.Stock}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StockDto implements Serializable {
        Integer id;
        ProductDto product;
        Integer categoryId;
        ProductUserResponse.ProductStockResponse.StockColorResponse color;
        BigDecimal price;
        Set<ProductUserResponse.ProductStockResponse.StockPhotoResponse> productPhotos;
        Set<ProductUserResponse.ProductStockResponse.StockInstanceResponse> instanceProperties;

        /**
         * DTO for {@link com.web.appleshop.entity.Product}
         */
        @Value
        public static class ProductDto implements Serializable {
            Integer id;
            String name;
        }
    }
}