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

/**
 * Handles administrative operations for promotions.
 * <p>
 * This controller provides endpoints for creating, reading, updating, deleting (CRUD),
 * searching, and managing the status of promotions from an administrator's perspective.
 */
@RestController
@RequestMapping("admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {
    private final PromotionService promotionService;

    /**
     * Creates a new promotion.
     *
     * @param request The request body containing the details of the promotion to create.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createPromotion(@Valid @RequestBody CreatePromotionRequest request) {
        promotionService.createPromotion(request);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Create promotion successfully")
        );
    }

    /**
     * Retrieves a paginated list of all promotions for the admin panel.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of promotions per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of {@link AdminPromotionDto}.
     */
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

    /**
     * Searches for promotions based on specified criteria.
     *
     * @param page The page number for pagination (optional, defaults to 0).
     * @param size The page size for pagination (optional, defaults to 6).
     * @param request The search criteria.
     * @return A {@link ResponseEntity} with a paginated list of found promotions.
     */
    @PostMapping("search")
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

    /**
     * Updates an existing promotion.
     *
     * @param id The ID of the promotion to update.
     * @param request The request body with the updated promotion details.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updatePromotion(
            @PathVariable Integer id,
            @Valid @RequestBody UpdatePromotionRequest request) {
        promotionService.updatePromotion(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Promotion updated successfully"));
    }

    /**
     * Deletes a promotion by its ID.
     *
     * @param id The ID of the promotion to delete.
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(@PathVariable Integer id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Promotion deleted successfully"));
    }

    /**
     * Toggles the active/inactive status of a promotion.
     *
     * @param id The ID of the promotion to toggle.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> togglePromotionStatus(@PathVariable Integer id) {
        promotionService.togglePromotionStatus(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Promotion status updated successfully"));
    }
}
