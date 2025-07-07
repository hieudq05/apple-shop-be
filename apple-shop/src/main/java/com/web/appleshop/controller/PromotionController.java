package com.web.appleshop.controller;

import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.UserPromotionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("promotions")
@RequiredArgsConstructor
public class PromotionController {
    @GetMapping("product/{productId}")
    public ResponseEntity<ApiResponse<List<UserPromotionDto>>> getPromotionsByProductId(@PathVariable Integer productId) {
        return ResponseEntity.ok(ApiResponse.success(null, "Get promotions successfully"));
    }
}
