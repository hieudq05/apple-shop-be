package com.web.appleshop.controller;

import com.web.appleshop.dto.request.UserCreateReviewRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createReviewForUser(
            @Valid @RequestBody UserCreateReviewRequest request
    ) {
        reviewService.userCreateReview(request);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Create review successfully")
        );
    }
}
