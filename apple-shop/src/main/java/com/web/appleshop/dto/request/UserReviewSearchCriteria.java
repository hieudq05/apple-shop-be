package com.web.appleshop.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserReviewSearchCriteria extends BaseReviewSearchCriteria {
    private Boolean isApproved; // User chỉ có thể filter theo approved (chỉ xem approved reviews)
    private Boolean hasReply; // Có reply hay không
    private Integer stockId; // ID của stock (user có thể xem reviews của product cụ thể)
    private Integer productId; // ID của product (user có thể xem reviews của product cụ thể)

    @Override
    public boolean isUserSpecific() {
        return true;
    }
}
