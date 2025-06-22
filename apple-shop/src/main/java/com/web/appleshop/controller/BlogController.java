package com.web.appleshop.controller;

import com.web.appleshop.entity.Blog;
import com.web.appleshop.dto.request.BlogRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Blog>> create(@Valid @RequestBody BlogRequest request) {
        Blog blog = blogService.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Tạo blog thành công", blog));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<Blog>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Blog> blogs = blogService.findAllPaginated(page, size);
        return ResponseEntity.ok(ApiResponse.ok("Danh sách blog", blogs));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Blog>> getById(@PathVariable Integer id) {
        Blog blog = blogService.findById(id).orElse(null);
        if (blog == null) {
            return ResponseEntity
                    .status(404)
                    .body(ApiResponse.fail("Không tìm thấy blog với ID = " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Chi tiết blog", blog));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Blog>> update(@PathVariable Integer id, @Valid @RequestBody BlogRequest request) {
        Blog blog = blogService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật blog thành công", blog));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        blogService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa blog thành công", null));
    }
}
