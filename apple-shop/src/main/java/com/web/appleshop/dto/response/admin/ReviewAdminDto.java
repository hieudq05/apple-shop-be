package com.web.appleshop.dto.response.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.Review}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewAdminDto implements Serializable {
    Integer id;
    ReviewAdminSummaryDto.UserDto user;
    String content;
    Integer rating;
    LocalDateTime createdAt;
    Boolean isApproved;
    ReviewAdminSummaryDto.UserDto approvedBy;
    LocalDateTime approvedAt;
    String replyContent;

    ReviewAdminSummaryDto.UserDto repliedBy;
    @NotNull
    ReviewAdminDto.StockDto stock;

    /**
     * DTO for {@link com.web.appleshop.entity.Stock}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StockDto implements Serializable {
        Integer id;
        ProductDto product;
        Set<ProductPhotoDto> productPhotos;

        /**
         * DTO for {@link com.web.appleshop.entity.Product}
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ProductDto implements Serializable {
            Integer id;
            String name;
        }

        /**
         * DTO for {@link com.web.appleshop.entity.ProductPhoto}
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ProductPhotoDto implements Serializable {
            Integer id;
            String imageUrl;
            String alt;
        }
    }
}