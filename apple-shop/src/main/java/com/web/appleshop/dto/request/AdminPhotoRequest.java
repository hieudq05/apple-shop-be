package com.web.appleshop.dto.request;

import lombok.Data;

@Data
public class AdminPhotoRequest {
    private String alt;
    private String imageUrl;
    private Integer stockId;
}
