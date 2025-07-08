package com.web.appleshop.dto.request;

import lombok.Data;

@Data
public class AdminCategoryRequest {
    private String name;
    private String description;
    private String image;
}
