package com.web.appleshop.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.Stock}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSummaryDto implements Serializable {
    Integer id;
    Integer productId;
    Integer colorId;
    String colorName;
    String colorHexCode;
    Integer quantity;
    BigDecimal price;
    Set<ProductPhotoDto> productPhotos;
    Set<InstancePropertyDto> instanceProperties;

    public StockSummaryDto(Integer id, Integer productId, Integer colorId, String colorName, String colorHexCode, Integer quantity, BigDecimal price) {
        this.id = id;
        this.productId = productId;
        this.colorId = colorId;
        this.colorName = colorName;
        this.colorHexCode = colorHexCode;
        this.quantity = quantity;
        this.price = price;
        this.productPhotos = new LinkedHashSet<>();
        this.instanceProperties = new LinkedHashSet<>();
    }

    /**
     * DTO for {@link com.web.appleshop.entity.ProductPhoto}
     */
    @Value
    public static class ProductPhotoDto implements Serializable {
        Integer id;
        String imageUrl;
        String alt;
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