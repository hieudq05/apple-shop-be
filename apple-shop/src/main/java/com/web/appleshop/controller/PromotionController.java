package com.web.appleshop.controller;

import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.UserPromotionDto;
import com.web.appleshop.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping("stock/{stockId}")
    public ResponseEntity<ApiResponse<List<UserPromotionDto>>> getPromotionsByProductId(
            @PathVariable Integer stockId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<UserPromotionDto> promotions = promotionService.getPromotionByStockForUser(stockId, pageable);
        PageableResponse pageableResponse = new PageableResponse(
                promotions.getNumber(),
                promotions.getSize(),
                promotions.getTotalPages(),
                promotions.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(promotions.getContent(), "Get promotions successfully", pageableResponse));
    }
}
