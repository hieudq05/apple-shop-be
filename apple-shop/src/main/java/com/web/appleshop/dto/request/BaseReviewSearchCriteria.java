package com.web.appleshop.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseReviewSearchCriteria {
    private String content; // Tìm kiếm theo nội dung review
    private Integer minRating; // Rating tối thiểu
    private Integer maxRating; // Rating tối đa
    private LocalDateTime createdAtFrom; // Từ ngày tạo
    private LocalDateTime createdAtTo; // Đến ngày tạo
    private String searchTerm; // Tìm kiếm tổng quát
    private String productName; // Tìm kiếm theo tên product
    private String userName; // Tìm kiếm theo tên user

    public abstract boolean isUserSpecific();
}
