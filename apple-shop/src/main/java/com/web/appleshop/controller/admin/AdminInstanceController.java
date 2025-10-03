package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.request.AdminInstancePropertyRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.service.InstanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles administrative operations for product instances.
 * <p>
 * A "product instance" can represent a specific variation of a product,
 * such as storage size (e.g., 128GB, 256GB). This controller provides
 * CRUD functionalities for managing these instances.
 */
@RestController
@RequestMapping("admin/instances")
@RequiredArgsConstructor
class AdminInstanceController {
    private final InstanceService instanceService;

    /**
     * Retrieves a paginated list of all product instances for the admin panel.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of instances per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of instance properties.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductAdminResponse.ProductStockAdminResponse.InstancePropertyDto>>> getAllInstances(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<ProductAdminResponse.ProductStockAdminResponse.InstancePropertyDto> instances = instanceService.getInstancesForAdmin(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                instances.getNumber(),
                instances.getSize(),
                instances.getTotalPages(),
                instances.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(instances.getContent(), "Get all instances successfully", pageableResponse));
    }

    /**
     * Creates a new product instance property.
     *
     * @param instance The request body containing the details of the instance to create.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createInstance(@Valid @RequestBody AdminInstancePropertyRequest instance) {
        instanceService.createInstance(instance);
        return ResponseEntity.ok(ApiResponse.success(null, "Create instance successfully"));
    }

    /**
     * Updates an existing product instance property.
     *
     * @param instanceId The ID of the instance to update.
     * @param instance The request body with the updated instance details.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping("{instanceId}")
    public ResponseEntity<ApiResponse<String>> updateInstance(@PathVariable Integer instanceId, @Valid @RequestBody AdminInstancePropertyRequest instance) {
        instanceService.updateInstance(instanceId, instance);
        return ResponseEntity.ok(ApiResponse.success(null, "Update instance successfully"));
    }

    /**
     * Deletes a product instance property by its ID.
     *
     * @param instanceId The ID of the instance to delete.
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping("{instanceId}")
    public ResponseEntity<ApiResponse<String>> deleteInstance(@PathVariable Integer instanceId) {
        instanceService.deleteInstance(instanceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Delete instance successfully"));
    }
}
