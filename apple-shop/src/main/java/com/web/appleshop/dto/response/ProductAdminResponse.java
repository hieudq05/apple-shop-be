package com.web.appleshop.dto.response;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.Product}
 */
@Value
@Data
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
        String username;
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
        String username;
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
        BigDecimal price;
    }
}