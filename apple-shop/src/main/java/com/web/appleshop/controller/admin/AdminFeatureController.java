package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.projection.FeatureInfoView;
import com.web.appleshop.dto.request.AdminFeatureRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.service.FeatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Handles administrative operations for product features.
 * <p>
 * This controller provides CRUD (Create, Read, Update, Delete) functionalities
 * for managing product features, which can be used to highlight key aspects
 * of products.
 */
@RestController
@RequestMapping("admin/features")
@RequiredArgsConstructor
class AdminFeatureController {
    private final FeatureService featureService;

    /**
     * Retrieves a paginated list of all product features for the admin panel.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of features per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of {@link FeatureInfoView}.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FeatureInfoView>>> getAllFeatures(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<FeatureInfoView> features = featureService.getFeaturesForAdmin(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                features.getNumber(),
                features.getSize(),
                features.getTotalPages(),
                features.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.success(features.getContent(), "Get all features successfully", pageableResponse)
        );
    }

    /**
     * Creates a new product feature.
     *
     * @param request The request body containing the feature details.
     * @param image The image file associated with the feature.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<String>> createFeature(@Valid @RequestPart AdminFeatureRequest request, @RequestPart MultipartFile image) {
        featureService.createFeature(request, image);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Create feature successfully")
        );
    }

    /**
     * Updates an existing product feature.
     *
     * @param featureId The ID of the feature to update.
     * @param request The request body with the updated feature details.
     * @param image The new image file for the feature.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping("{featureId}")
    public ResponseEntity<ApiResponse<String>> updateFeature(@PathVariable Integer featureId, @Valid @RequestPart AdminFeatureRequest request, @RequestPart MultipartFile image) {
        featureService.updateFeature(featureId, request, image);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Update feature successfully")
        );
    }

    /**
     * Deletes a product feature by its ID.
     *
     * @param featureId The ID of the feature to delete.
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping("{featureId}")
    public ResponseEntity<ApiResponse<String>> deleteFeature(@PathVariable Integer featureId) {
        featureService.deleteFeature(featureId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Delete feature successfully")
        );
    }
}
