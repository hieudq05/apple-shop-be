package com.web.appleshop.controller;

import com.web.appleshop.dto.request.UserCreateReviewRequest;
import com.web.appleshop.dto.request.UserReviewSearchCriteria;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.UserReviewDto;
import com.web.appleshop.service.ReviewService;
import com.web.appleshop.service.UserReviewSearchService;
import com.web.appleshop.specification.UserReviewSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles HTTP requests related to product reviews.
 * <p>
 * This controller provides endpoints for users to create, view, search, and delete
 * their reviews. It also allows public access to view reviews for a specific product.
 */
@RestController
@RequestMapping("reviews")
@RequiredArgsConstructor
public class ReviewController {
    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);
    private final ReviewService reviewService;
    private final UserReviewSearchService userReviewSearchService;
    private final UserReviewSpecification userReviewSpecification;

    /**
     * Creates a new review for a product by an authenticated user.
     *
     * @param request The request body containing the review details.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createReviewForUser(
            @Valid @RequestBody UserCreateReviewRequest request
    ) {
        reviewService.userCreateReview(request);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Create review successfully")
        );
    }

    /**
     * Retrieves a paginated list of reviews for a specific product.
     * <p>
     * If an authentication token is provided, it returns all reviews. Otherwise, it
     * returns only the approved reviews.
     *
     * @param productId The ID of the product.
     * @param token The Authorization token (optional).
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of reviews per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of {@link UserReviewDto}.
     */
    @GetMapping("product/{productId}")
    public ResponseEntity<ApiResponse<List<UserReviewDto>>> getReviewsForProduct(
            @PathVariable Integer productId,
            @RequestHeader(name = "Authorization", required = false) String token,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<UserReviewDto> reviews;
        if (token == null) {
            reviews = userReviewSearchService.getProductApprovedReviews(productId, pageable);
        } else {
            reviews = userReviewSearchService.getProductReviews(productId, pageable);
        }

        PageableResponse pageableResponse = new PageableResponse(
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalPages(),
                reviews.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.success(reviews.getContent(), "Get reviews for product successfully", pageableResponse)
        );
    }

    /**
     * Retrieves a paginated list of reviews created by the currently authenticated user.
     *
     * @return A {@link ResponseEntity} containing a paginated list of the user's {@link UserReviewDto}s.
     */
    @GetMapping("my")
    public ResponseEntity<ApiResponse<List<UserReviewDto>>> getReviewsForUser() {
        Pageable pageable = Pageable.ofSize(6).withPage(0);
        Page<UserReviewDto> reviews = userReviewSearchService.getMyReviews(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalPages(),
                reviews.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.success(reviews.getContent(), "Get reviews for user successfully", pageableResponse)
        );
    }

    /**
     * Deletes a review created by the currently authenticated user.
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
     * Searches for reviews of a specific product based on given criteria.
     *
     * @param productId The ID of the product.
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of reviews per page (optional, defaults to 6).
     * @param criteria The search criteria.
     * @return A {@link ResponseEntity} containing a paginated list of matching {@link UserReviewDto}s.
     */
    @GetMapping("product/{productId}/search")
    public ResponseEntity<ApiResponse<List<UserReviewDto>>> searchReviewsForProduct(
            @PathVariable Integer productId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestBody UserReviewSearchCriteria criteria
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<UserReviewDto> reviews = userReviewSearchService.searchReviewsForGuest(criteria, pageable);
        PageableResponse pageableResponse = new PageableResponse(
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalPages(),
                reviews.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.success(reviews.getContent(), "Search reviews for product successfully", pageableResponse)
        );
    }
}
