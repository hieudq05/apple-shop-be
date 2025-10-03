package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.projection.BlogInfo;
import com.web.appleshop.dto.projection.BlogSummaryInfo;
import com.web.appleshop.dto.request.CreateBlogRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles administrative operations related to blogs.
 * <p>
 * This controller provides endpoints for creating, reading, updating, deleting (CRUD),
 * and managing blog posts from an administrator's perspective. It includes
 * functionalities like publishing/unpublishing blogs and viewing statistics.
 */
@RestController
@RequestMapping("admin/blogs")
@RequiredArgsConstructor
class AdminBlogController {
    private final BlogService blogService;

    /**
     * Retrieves a paginated list of all blogs for the admin panel.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of blogs per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of {@link BlogSummaryInfo}.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BlogSummaryInfo>>> getBlogs(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 6, sort);
        Page<BlogSummaryInfo> blogs = blogService.getListBlogsForAdmin(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                blogs.getNumber(),
                blogs.getSize(),
                blogs.getTotalPages(),
                blogs.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(blogs.getContent(), "Get all blogs successfully", pageableResponse));
    }

    /**
     * Creates a new blog post.
     *
     * @param createBlogRequest The request body containing the blog's title, content, etc.
     * @param fileImage The image file for the blog post.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<String>> createBlog(@Valid @RequestPart CreateBlogRequest createBlogRequest, @RequestPart MultipartFile fileImage) {
        blogService.createBlog(createBlogRequest, fileImage);
        return ResponseEntity.ok(ApiResponse.success(null, "Create blog successfully"));
    }

    /**
     * Updates an existing blog post.
     *
     * @param blogId The ID of the blog to update.
     * @param updateBlogRequest The request body with the updated blog details.
     * @param fileImage The new image file for the blog post (optional).
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping(consumes = "multipart/form-data", path = "{blogId}")
    public ResponseEntity<ApiResponse<String>> updateBlog(@PathVariable Integer blogId, @Valid @RequestPart CreateBlogRequest updateBlogRequest, @RequestPart MultipartFile fileImage) {
        blogService.updateBlog(blogId, updateBlogRequest, fileImage);
        return ResponseEntity.ok(ApiResponse.success(null, "Update blog successfully"));
    }

    /**
     * Deletes a blog post by its ID.
     *
     * @param blogId The ID of the blog to delete.
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping("{blogId}")
    public ResponseEntity<ApiResponse<String>> deleteBlog(@PathVariable Integer blogId) {
        blogService.deleteBlog(blogId);
        return ResponseEntity.ok(ApiResponse.success(null, "Delete blog successfully"));
    }

    /**
     * Retrieves a single blog's details by its ID for the admin panel.
     *
     * @param blogId The ID of the blog to retrieve.
     * @return A {@link ResponseEntity} containing the {@link BlogInfo}.
     */
    @GetMapping("{blogId}")
    public ResponseEntity<ApiResponse<BlogInfo>> getBlogById(@PathVariable Integer blogId) {
        BlogInfo blog = blogService.getBlogByIdForAdmin(blogId);
        return ResponseEntity.ok(ApiResponse.success(blog, "Get blog successfully"));
    }

    /**
     * Toggles the published status of a blog post.
     *
     * @param blogId The ID of the blog to toggle.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping("{blogId}/toggle-publish")
    public ResponseEntity<ApiResponse<String>> togglePublishBlog(@PathVariable Integer blogId) {
        blogService.togglePublish(blogId);
        return ResponseEntity.ok(ApiResponse.success(null, "Toggle publish blog successfully"));
    }

    /**
     * Retrieves the count of published blogs within a specified date range.
     *
     * @param fromDate The start of the date range (optional).
     * @param toDate The end of the date range (optional).
     * @return A {@link ResponseEntity} containing the count of published blogs.
     */
    @GetMapping("statistics/published-count")
    public ResponseEntity<ApiResponse<Long>> getPublishCount(
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate
    ) {
        Long publishCount = blogService.getPublishedBlogCount(fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success(publishCount, "Get publish count successfully"));
    }
}
