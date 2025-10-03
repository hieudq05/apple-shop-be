package com.web.appleshop.controller;

import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.UserReviewDto;
import com.web.appleshop.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Handles HTTP requests related to product colors.
 * <p>
 * This controller provides an endpoint for retrieving a list of all available
 * product colors. This is typically used to populate color selection options in
 * the user interface.
 */
@RestController
@RequestMapping("colors")
@RequiredArgsConstructor
public class ColorController {
    private final ColorService colorService;

    /**
     * Retrieves a list of all product colors.
     *
     * @return A {@link ResponseEntity} containing a list of {@link UserReviewDto.StockDto.ColorDto}.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserReviewDto.StockDto.ColorDto>>> getColors() {
        return ResponseEntity.ok(ApiResponse.success(colorService.getAllColors(), "Get colors successfully"));
    }
}
