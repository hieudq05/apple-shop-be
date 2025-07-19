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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final UserReviewSearchService userReviewSearchService;
    private final UserReviewSpecification userReviewSpecification;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createReviewForUser(
            @Valid @RequestBody UserCreateReviewRequest request
    ) {
        reviewService.userCreateReview(request);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Create review successfully")
        );
    }

    @GetMapping("product/{productId}")
    public ResponseEntity<ApiResponse<List<UserReviewDto>>> getReviewsForProduct(@PathVariable Integer productId, @RequestHeader(name = "Authorization", required = false) String token) {
        Pageable pageable = Pageable.ofSize(6).withPage(0);
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

    @DeleteMapping("{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(@PathVariable Integer reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Delete review successfully")
        );
    }

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
