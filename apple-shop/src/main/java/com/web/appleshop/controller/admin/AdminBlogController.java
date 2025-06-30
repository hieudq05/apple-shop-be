package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.projection.BlogInfo;
import com.web.appleshop.dto.projection.BlogSummaryInfo;
import com.web.appleshop.dto.request.CreateBlogRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.entity.Blog;
import com.web.appleshop.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("admin/blogs")
@RequiredArgsConstructor
class AdminBlogController {
    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BlogSummaryInfo>>> getBlogs(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<BlogSummaryInfo> blogs = blogService.getListBlogsForAdmin(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                blogs.getNumber(),
                blogs.getSize(),
                blogs.getTotalPages(),
                blogs.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(blogs.getContent(), "Get all blogs successfully", pageableResponse));
    }

    @PostMapping(consumes = "multipart/form-data")
        public ResponseEntity<ApiResponse<String>> createBlog(@Valid @RequestPart CreateBlogRequest createBlogRequest, @RequestPart MultipartFile fileImage) {
        blogService.createBlog(createBlogRequest, fileImage);
        return ResponseEntity.ok(ApiResponse.success(null, "Create blog successfully"));
    }

    @PutMapping(consumes = "multipart/form-data", path = "{blogId}")
    public ResponseEntity<ApiResponse<String>> updateBlog(@PathVariable Integer blogId, @Valid @RequestPart CreateBlogRequest createBlogRequest, @RequestPart MultipartFile fileImage) {
        blogService.updateBlog(blogId, createBlogRequest, fileImage);
        return ResponseEntity.ok(ApiResponse.success(null, "Update blog successfully"));
    }

    @DeleteMapping("{blogId}")
    public ResponseEntity<ApiResponse<String>> deleteBlog(@PathVariable Integer blogId) {
        blogService.deleteBlog(blogId);
        return ResponseEntity.ok(ApiResponse.success(null, "Delete blog successfully"));
    }

    @GetMapping("{blogId}")
    public ResponseEntity<ApiResponse<BlogInfo>> getBlogById(@PathVariable Integer blogId) {
        BlogInfo blog = blogService.getBlogByIdForAdmin(blogId);
        return ResponseEntity.ok(ApiResponse.success(blog, "Get blog successfully"));
    }

    @PutMapping("{blogId}/toggle-publish")
    public ResponseEntity<ApiResponse<String>> togglePublishBlog(@PathVariable Integer blogId) {
        blogService.togglePublish(blogId);
        return ResponseEntity.ok(ApiResponse.success(null, "Toggle publish blog successfully"));
    }

}
