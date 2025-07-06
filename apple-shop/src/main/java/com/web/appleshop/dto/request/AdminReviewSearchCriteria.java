package com.web.appleshop.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminReviewSearchCriteria extends BaseReviewSearchCriteria {
    private Boolean isApproved; // Trạng thái approved
    private String approvedByName; // Tên người approve
    private LocalDateTime approvedAtFrom; // Từ ngày approve
    private LocalDateTime approvedAtTo; // Đến ngày approve
    private String replyContent; // Nội dung reply
    private String repliedByName; // Tên người reply
    private Boolean hasReply; // Có reply hay không
    private Integer userId; // ID của user (để tìm reviews của user cụ thể)
    private Integer productId; // ID của product (để tìm reviews của product cụ thể)
    private String categoryName; // Tìm theo category của product

    @Override
    public boolean isUserSpecific() {
        return false;
    }
}
