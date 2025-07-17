package com.web.appleshop.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.web.appleshop.entity.Review}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewAdminSummaryDto implements Serializable {
    Integer id;
    UserDto user;
    Integer rating;
    LocalDateTime createdAt;
    Boolean isApproved;

    /**
     * DTO for {@link com.web.appleshop.entity.User}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto implements Serializable {
        Integer id;
        String email;
        String firstName;
        String lastName;
        String image;
    }
}