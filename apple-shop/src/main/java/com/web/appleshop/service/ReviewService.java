package com.web.appleshop.service;

import com.web.appleshop.dto.request.ApproveReviewRequest;
import com.web.appleshop.dto.request.BaseReviewSearchCriteria;
import com.web.appleshop.dto.request.ReplyReviewRequest;
import com.web.appleshop.dto.request.UserCreateReviewRequest;
import com.web.appleshop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ReviewService {
    Review userCreateReview(UserCreateReviewRequest request);

    Review approveReview(Integer reviewId, ApproveReviewRequest request);

    Review replyToReview(Integer reviewId, ReplyReviewRequest request);
}
