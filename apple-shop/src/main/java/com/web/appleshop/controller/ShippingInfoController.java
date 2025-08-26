package com.web.appleshop.controller;

import com.web.appleshop.dto.request.UserShippingInfoRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.UserShippingInfoDto;
import com.web.appleshop.service.ShippingInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("shipping-infos")
@RequiredArgsConstructor
public class ShippingInfoController {
    private final ShippingInfoService shippingInfoService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createShippingInfo(@Valid @RequestBody UserShippingInfoRequest shippingInfoRequest) {
        shippingInfoService.createShippingInfo(shippingInfoRequest);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Create shipping info successfully")
        );
    }

    @PutMapping("{shippingInfoId}")
    public ResponseEntity<ApiResponse<String>> updateShippingInfo(@PathVariable Integer shippingInfoId, @Valid @RequestBody UserShippingInfoRequest shippingInfoRequest) {
        shippingInfoService.updateShippingInfo(shippingInfoId, shippingInfoRequest);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Update shipping info successfully")
        );
    }

    @DeleteMapping("{shippingInfoId}")
    public ResponseEntity<ApiResponse<String>> deleteShippingInfo(@PathVariable Integer shippingInfoId) {
        shippingInfoService.deleteShippingInfo(shippingInfoId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Delete shipping info successfully")
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserShippingInfoDto>>> getShippingInfo(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        PageRequest pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 6, Sort.by("isDefault", "createdAt").descending());
        Page<UserShippingInfoDto> shippingInfos = shippingInfoService.getMyShippingInfo(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                shippingInfos.getNumber(),
                shippingInfos.getSize(),
                shippingInfos.getTotalPages(),
                shippingInfos.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.success(shippingInfos.getContent(), "Get shipping info successfully", pageableResponse)
        );
    }

    @PutMapping("{shippingInfoId}/default")
    public ResponseEntity<ApiResponse<String>> setDefaultShippingInfo(@PathVariable Integer shippingInfoId) {
        shippingInfoService.setDefaultShippingInfo(shippingInfoId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Set default shipping info successfully")
        );
    }
}
