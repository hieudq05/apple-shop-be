package com.web.appleshop.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.Review}
 */
@Value
public class UserReviewDto implements Serializable {
    Integer id;
    UserDto user;
    String content;
    Integer rating;
    LocalDateTime createdAt;
    String replyContent;
    UserDto repliedBy;
    Integer productId;
    UserReviewDto.StockDto stock;


    /**
     * DTO for {@link com.web.appleshop.entity.User}
     */
    @Value
    public static class UserDto implements Serializable {
        Integer id;
        String firstName;
        String lastName;
        String image;
    }

    /**
     * DTO for {@link com.web.appleshop.entity.Stock}
     */
    @Value
    public static class StockDto implements Serializable {
        Integer id;
        ColorDto color;
        Set<InstancePropertyDto> instanceProperties;

        /**
         * DTO for {@link com.web.appleshop.entity.Color}
         */
        @Value
        public static class ColorDto implements Serializable {
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
}