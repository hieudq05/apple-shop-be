package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.request.ApproveReviewRequest;
import com.web.appleshop.dto.request.ReplyReviewRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.ReviewAdminDto;
import com.web.appleshop.dto.response.admin.ReviewAdminSummaryDto;
import com.web.appleshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {
    private final ReviewService reviewService;

    @PutMapping("approve/{reviewId}")
    public ResponseEntity<ApiResponse<String>> approveReview(
            @PathVariable Integer reviewId
    ) {
        reviewService.approveReview(reviewId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Approve review successfully")
        );
    }

    @PutMapping("reply/{reviewId}")
    public ResponseEntity<ApiResponse<String>> replyReview(
            @PathVariable Integer reviewId,
            @RequestBody ReplyReviewRequest request
    ) {
        reviewService.replyToReview(reviewId, request);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Reply review successfully")
        );
    }

    @DeleteMapping("{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(@PathVariable Integer reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Delete review successfully")
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewAdminSummaryDto>>> getReviews(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Boolean isApproved
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<ReviewAdminSummaryDto> reviews = reviewService.getAllReviewsForAdmin(pageable, isApproved);
        PageableResponse pageableResponse = new PageableResponse(
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalPages(),
                reviews.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.success(reviews.getContent(), "Get reviews successfully", pageableResponse)
        );
    }

    @GetMapping("{reviewId}")
    public ResponseEntity<ApiResponse<ReviewAdminDto>> getReviewById(@PathVariable Integer reviewId) {
        ReviewAdminDto review = reviewService.getReviewDetail(reviewId);
        return ResponseEntity.ok(
                ApiResponse.success(review, "Get review successfully")
        );
    }

    @GetMapping("statistics/review-count")
    public ResponseEntity<ApiResponse<Long>> getReviewCount(
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        Long reviewCount = reviewService.getReviewCount(fromDate, toDate);
        return ResponseEntity.ok(
                ApiResponse.success(reviewCount, "Get review count successfully")
        );
    }

    @GetMapping("statistics/avg-review/{productId}")
    public ResponseEntity<ApiResponse<Double>> getProductCount(
            @PathVariable Integer productId
    ) {
        Double avgReview = reviewService.getReviewAverage(productId);
        return ResponseEntity.ok(
                ApiResponse.success(avgReview, "Get review count successfully")
        );
    }

}
