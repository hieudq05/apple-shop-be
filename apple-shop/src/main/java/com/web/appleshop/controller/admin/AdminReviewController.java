package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.request.ApproveReviewRequest;
import com.web.appleshop.dto.request.ReplyReviewRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {
    private final ReviewService reviewService;

    @PutMapping("approve/{reviewId}")
    public ResponseEntity<ApiResponse<String>> approveReview(
            @PathVariable Integer reviewId,
            @RequestBody ApproveReviewRequest request
    ) {
        reviewService.approveReview(reviewId, request);
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
}
