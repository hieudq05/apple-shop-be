package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.request.CreatePromotionRequest;
import com.web.appleshop.dto.request.PromotionSearchRequest;
import com.web.appleshop.dto.request.UpdatePromotionRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.AdminPromotionDto;
import com.web.appleshop.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {
    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createPromotion(@Valid @RequestBody CreatePromotionRequest request) {
        promotionService.createPromotion(request);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Create promotion successfully")
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminPromotionDto>>> getAllPromotions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<AdminPromotionDto> promotions = promotionService.searchPromotions(new PromotionSearchRequest(), pageable);
        PageableResponse pageableResponse = new PageableResponse(
                promotions.getNumber(),
                promotions.getSize(),
                promotions.getTotalPages(),
                promotions.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.success(promotions.getContent(), "Get all promotions successfully", pageableResponse)
        );
    }

    @GetMapping("search")
    public ResponseEntity<ApiResponse<List<AdminPromotionDto>>> searchPromotions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestBody PromotionSearchRequest request
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<AdminPromotionDto> promotions = promotionService.searchPromotions(request, pageable);
        PageableResponse pageableResponse = new PageableResponse(
                promotions.getNumber(),
                promotions.getSize(),
                promotions.getTotalPages(),
                promotions.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.success(promotions.getContent(), "Search promotions successfully", pageableResponse)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updatePromotion(
            @PathVariable Integer id,
            @Valid @RequestBody UpdatePromotionRequest request) {
        promotionService.updatePromotion(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Promotion updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(@PathVariable Integer id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Promotion deleted successfully"));
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> togglePromotionStatus(@PathVariable Integer id) {
        promotionService.togglePromotionStatus(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Promotion status updated successfully"));
    }
}
