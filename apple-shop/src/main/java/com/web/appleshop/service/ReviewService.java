package com.web.appleshop.service;

import com.web.appleshop.dto.request.ApproveReviewRequest;
import com.web.appleshop.dto.request.BaseReviewSearchCriteria;
import com.web.appleshop.dto.request.ReplyReviewRequest;
import com.web.appleshop.dto.request.UserCreateReviewRequest;
import com.web.appleshop.dto.response.admin.ReviewAdminDto;
import com.web.appleshop.dto.response.admin.ReviewAdminSummaryDto;
import com.web.appleshop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public interface ReviewService {
    Review userCreateReview(UserCreateReviewRequest request);

    Review approveReview(Integer reviewId);

    Review replyToReview(Integer reviewId, ReplyReviewRequest request);

    void deleteReview(Integer reviewId);

    Long getReviewCount(LocalDateTime fromDate, LocalDateTime toDate);

    Double getReviewAverage(Integer productId);

    Page<ReviewAdminSummaryDto> getAllReviewsForAdmin(Pageable pageable, Boolean isApproved);

    ReviewAdminDto getReviewDetail(Integer reviewId);
}
