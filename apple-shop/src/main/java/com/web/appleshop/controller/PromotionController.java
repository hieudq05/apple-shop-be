package com.web.appleshop.controller;

import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.UserPromotionDto;
import com.web.appleshop.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Handles HTTP requests related to promotions.
 * <p>
 * This controller provides an endpoint for users to retrieve a list of available
 * promotions. The promotions can be used to get discounts on orders.
 */
@RestController
@RequestMapping("promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    /**
     * Retrieves a paginated list of promotions available to users.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of promotions per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of {@link UserPromotionDto}.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserPromotionDto>>> getPromotions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<UserPromotionDto> promotions = promotionService.getPromotionsForUser(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                promotions.getNumber(),
                promotions.getSize(),
                promotions.getTotalPages(),
                promotions.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.success(promotions.getContent(), "Get promotions successfully", pageableResponse)
        );
    }
}
