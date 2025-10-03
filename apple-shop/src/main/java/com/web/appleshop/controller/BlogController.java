package com.web.appleshop.controller;

import com.web.appleshop.dto.projection.BlogForUserInfo;
import com.web.appleshop.dto.projection.BlogInfo;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles HTTP requests related to blogs.
 * <p>
 * This controller provides endpoints for retrieving a list of blogs and fetching a
 * specific blog by its ID. The endpoints are publicly accessible and intended for
 * general users of the application.
 */
@RestController
@RequestMapping("blogs")
@RequiredArgsConstructor
class BlogController {
    private final BlogService blogService;

    /**
     * Retrieves a paginated list of blogs for users.
     * <p>
     * This endpoint supports pagination through the {@code page} and {@code size}
     * request parameters. If not provided, it defaults to the first page with a
     * size of 6.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of blogs per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of {@link BlogForUserInfo}.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BlogForUserInfo>>> getListBlogForUser(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<BlogForUserInfo> blogs = blogService.getListBlogsForUser(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                blogs.getNumber(),
                blogs.getSize(),
                blogs.getTotalPages(),
                blogs.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(blogs.getContent(), "Get all blogs successfully", pageableResponse));
    }

    /**
     * Retrieves a single blog by its unique identifier.
     *
     * @param blogId The ID of the blog to retrieve.
     * @return A {@link ResponseEntity} containing the {@link BlogInfo} if found.
     */
    @GetMapping("{blogId}")
    public ResponseEntity<ApiResponse<BlogInfo>> getBlogById(@PathVariable Integer blogId) {
        BlogInfo blog = blogService.getBlogByIdForUser(blogId);
        return ResponseEntity.ok(ApiResponse.success(blog, "Get blog successfully"));
    }
}
