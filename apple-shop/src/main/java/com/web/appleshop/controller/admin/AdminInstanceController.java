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

@RestController
@RequestMapping("admin/instances")
@RequiredArgsConstructor
class AdminInstanceController {
    private final InstanceService instanceService;

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

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createInstance(@Valid @RequestBody AdminInstancePropertyRequest instance) {
        instanceService.createInstance(instance);
        return ResponseEntity.ok(ApiResponse.success(null, "Create instance successfully"));
    }

    @PutMapping("{instanceId}")
    public ResponseEntity<ApiResponse<String>> updateInstance(@PathVariable Integer instanceId, @Valid @RequestBody AdminInstancePropertyRequest instance) {
        instanceService.updateInstance(instanceId, instance);
        return ResponseEntity.ok(ApiResponse.success(null, "Update instance successfully"));
    }

    @DeleteMapping("{instanceId}")
    public ResponseEntity<ApiResponse<String>> deleteInstance(@PathVariable Integer instanceId) {
        instanceService.deleteInstance(instanceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Delete instance successfully"));
    }
}
