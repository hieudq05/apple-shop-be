package com.web.appleshop.dto.response.admin;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserAdminSummaryDto(
        Integer id,
        String email,
        String phone,
        String firstName,
        String lastName,
        String image,
        Boolean enabled,
        LocalDate birth
) {
}
