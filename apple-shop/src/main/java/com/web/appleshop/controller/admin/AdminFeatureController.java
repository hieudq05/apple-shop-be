package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.projection.FeatureInfoView;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.service.FeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/features")
@RequiredArgsConstructor
class AdminFeatureController {
    private final FeatureService featureService;

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
}
