package com.web.appleshop.dto.response;

import com.web.appleshop.enums.PromotionType;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.CartItem}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse implements Serializable {
    Integer id;
    ProductDto product;
    StockDto stock;
    Integer quantity;

    /**
     * DTO for {@link com.web.appleshop.entity.Product}
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductDto implements Serializable {
        Integer id;
        String name;
        String description;
        Integer categoryId;
    }

    /**
     * DTO for {@link com.web.appleshop.entity.Stock}
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StockDto implements Serializable {
        Integer id;
        ProductUserResponse.ProductStockResponse.StockColorResponse color;
        Integer quantity;
        BigDecimal price;
        Set<ProductUserResponse.ProductStockResponse.StockPhotoResponse> productPhotos;
        Set<ProductUserResponse.ProductStockResponse.StockInstanceResponse> instance;
    }
}