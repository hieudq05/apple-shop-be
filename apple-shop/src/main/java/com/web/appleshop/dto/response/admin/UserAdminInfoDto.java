package com.web.appleshop.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.web.appleshop.entity.User}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAdminInfoDto implements Serializable {
    Integer id;
    String email;
    String phone;
    String firstName;
    String lastName;
    String image;
    LocalDateTime createdAt;
    Boolean enabled;
    LocalDate birth;
    Set<RoleDto> roles;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RoleDto {
        Integer id;
        String name;
    }
}

