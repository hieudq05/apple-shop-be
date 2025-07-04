package com.web.appleshop.dto.request;

import lombok.Data;

@Data
public class CategorySearchCriteria {
    private Integer id;
    private String name;
    private String searchTerm;
    private Boolean hasProducts;
    private Boolean hasPromotions;
    private Integer minProductCount;
    private Integer maxProductCount;
    private String promotionName;
    private Boolean hasActivePromotions;
}
