package com.web.appleshop.controller.admin;

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

/**
 * Handles administrative operations for product reviews.
 * <p>
 * This controller provides endpoints for administrators to manage product reviews,
 * including approving, replying to, deleting, and viewing them. It also offers
 * statistical endpoints related to reviews.
 */
@RestController
@RequestMapping("admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {
    private final ReviewService reviewService;

    /**
     * Approves a product review.
     *
     * @param reviewId The ID of the review to approve.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping("approve/{reviewId}")
    public ResponseEntity<ApiResponse<String>> approveReview(
            @PathVariable Integer reviewId
    ) {
        reviewService.approveReview(reviewId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Approve review successfully")
        );
    }

    /**
     * Adds an administrator's reply to a product review.
     *
     * @param reviewId The ID of the review to reply to.
     * @param request The request body containing the reply content.
     * @return A {@link ResponseEntity} with a success message.
     */
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

    /**
     * Deletes a product review.
     *
     * @param reviewId The ID of the review to delete.
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping("{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(@PathVariable Integer reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Delete review successfully")
        );
    }

    /**
     * Retrieves a paginated list of reviews for the admin panel, with an optional filter for approval status.
     *
     * @param page       The page number to retrieve (optional, defaults to 0).
     * @param size       The number of reviews per page (optional, defaults to 6).
     * @param isApproved An optional boolean to filter reviews by their approval status.
     * @return A {@link ResponseEntity} containing a paginated list of {@link ReviewAdminSummaryDto}.
     */
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

    /**
     * Retrieves the detailed information of a single review.
     *
     * @param reviewId The ID of the review to retrieve.
     * @return A {@link ResponseEntity} containing the {@link ReviewAdminDto}.
     */
    @GetMapping("{reviewId}")
    public ResponseEntity<ApiResponse<ReviewAdminDto>> getReviewById(@PathVariable Integer reviewId) {
        ReviewAdminDto review = reviewService.getReviewDetail(reviewId);
        return ResponseEntity.ok(
                ApiResponse.success(review, "Get review successfully")
        );
    }

    /**
     * Retrieves the total count of reviews within a specified date range.
     *
     * @param fromDate The start of the date range (optional).
     * @param toDate   The end of the date range (optional).
     * @return A {@link ResponseEntity} containing the total review count.
     */
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

    /**
     * Retrieves the average rating for a specific product.
     *
     * @param productId The ID of the product.
     * @return A {@link ResponseEntity} containing the average rating as a Double.
     */
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
