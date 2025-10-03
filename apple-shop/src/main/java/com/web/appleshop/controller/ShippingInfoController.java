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
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Manages shipping information for authenticated users.
 * <p>
 * This controller provides endpoints to create, update, delete, and view shipping
 * addresses associated with a user's account. It also allows setting a default
 * shipping address.
 */
@RestController
@RequestMapping("shipping-infos")
@RequiredArgsConstructor
public class ShippingInfoController {
    private final ShippingInfoService shippingInfoService;

    /**
     * Creates a new shipping address for the currently authenticated user.
     *
     * @param shippingInfoRequest The request body containing the new shipping address details.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createShippingInfo(@Valid @RequestBody UserShippingInfoRequest shippingInfoRequest) {
        shippingInfoService.createShippingInfo(shippingInfoRequest);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Create shipping info successfully")
        );
    }

    /**
     * Updates an existing shipping address for the user.
     *
     * @param shippingInfoId The ID of the shipping address to update.
     * @param shippingInfoRequest The request body with the updated details.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping("{shippingInfoId}")
    public ResponseEntity<ApiResponse<String>> updateShippingInfo(@PathVariable Integer shippingInfoId, @Valid @RequestBody UserShippingInfoRequest shippingInfoRequest) {
        shippingInfoService.updateShippingInfo(shippingInfoId, shippingInfoRequest);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Update shipping info successfully")
        );
    }

    /**
     * Deletes a shipping address for the user.
     *
     * @param shippingInfoId The ID of the shipping address to delete.
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping("{shippingInfoId}")
    public ResponseEntity<ApiResponse<String>> deleteShippingInfo(@PathVariable Integer shippingInfoId) {
        shippingInfoService.deleteShippingInfo(shippingInfoId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Delete shipping info successfully")
        );
    }

    /**
     * Retrieves a paginated list of shipping addresses for the currently authenticated user.
     * The list is sorted to show the default address first, then by creation date.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of addresses per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of {@link UserShippingInfoDto}.
     */
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

    /**
     * Sets a specific shipping address as the default for the user.
     *
     * @param shippingInfoId The ID of the shipping address to set as default.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping("{shippingInfoId}/default")
    public ResponseEntity<ApiResponse<String>> setDefaultShippingInfo(@PathVariable Integer shippingInfoId) {
        shippingInfoService.setDefaultShippingInfo(shippingInfoId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Set default shipping info successfully")
        );
    }
}
