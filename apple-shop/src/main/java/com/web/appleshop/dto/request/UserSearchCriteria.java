package com.web.appleshop.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserSearchCriteria {
    private Integer id;
    private String email;
    private String phone;
    private String name;
    private Boolean enabled;
    private LocalDate birthFrom;
    private LocalDate birthTo;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
    private Set<String> roleName;
}
