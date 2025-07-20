package com.web.appleshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryWProductResponse {
    private Integer id;
    private String name;
    private String description;
    private String image;
    private ProductInfo[] products;

    @Data
    @AllArgsConstructor
    public static class ProductInfo {
        private Integer id;
        private String name;
    }
}
