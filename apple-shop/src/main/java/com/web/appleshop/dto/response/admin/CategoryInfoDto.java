package com.web.appleshop.dto.response.admin;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryInfoDto {
    Integer id;
    String name;
    String description;
    String image;
}
