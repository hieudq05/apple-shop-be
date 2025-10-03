package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.request.AdminColorRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.service.ColorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles administrative operations for product colors.
 * <p>
 * This controller provides CRUD (Create, Read, Update, Delete) functionalities
 * for managing product colors in the admin panel.
 */
@RestController
@RequestMapping("admin/colors")
@RequiredArgsConstructor
class AdminColorController {
    private final ColorService colorService;

    /**
     * Retrieves a paginated list of all product colors for the admin panel.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of colors per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of color details.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse>>> getAllColors(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse> colors = colorService.getColorsForAdmin(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                colors.getNumber(),
                colors.getSize(),
                colors.getTotalPages(),
                colors.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.success(colors.getContent(), "Get all colors successfully", pageableResponse)
        );
    }

    /**
     * Creates a new product color.
     *
     * @param request The request body containing the details of the color to create.
     * @return A {@link ResponseEntity} with the created color's details.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse>> createColor(@Valid @RequestBody AdminColorRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(colorService.createColor(request), "Create color successfully")
        );
    }

    /**
     * Updates an existing product color.
     *
     * @param colorId The ID of the color to update.
     * @param request The request body containing the updated color details.
     * @return A {@link ResponseEntity} with the updated color's details.
     */
    @PutMapping("{colorId}")
    public ResponseEntity<ApiResponse<ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse>> updateColor(@PathVariable Integer colorId, @RequestBody AdminColorRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(colorService.updateColor(colorId, request), "Update color successfully")
        );
    }

    /**
     * Deletes a product color by its ID.
     *
     * @param colorId The ID of the color to delete.
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping("{colorId}")
    public ResponseEntity<ApiResponse<String>> deleteColor(@PathVariable Integer colorId) {
        colorService.deleteColor(colorId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Delete color successfully")
        );
    }
}
