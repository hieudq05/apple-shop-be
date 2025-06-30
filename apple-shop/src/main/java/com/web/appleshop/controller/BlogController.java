package com.web.appleshop.controller;

import com.web.appleshop.dto.projection.BlogForUserInfo;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.service.BlogService;
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
@RequestMapping("blogs")
@RequiredArgsConstructor
class BlogController {
    private final BlogService blogService;

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
}
