package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.service.InstanceService;
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
}
