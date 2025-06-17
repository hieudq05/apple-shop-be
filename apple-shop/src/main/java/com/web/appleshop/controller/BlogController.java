package com.web.appleshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.appleshop.dto.BlogResponse;
import com.web.appleshop.service.BlogService;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    @Autowired
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    // Fixed: Now properly calls service method
    @GetMapping("/{id}")
    public ResponseEntity<BlogResponse> getBlogById(@PathVariable Integer id) {
        BlogResponse blog = blogService.getBlogById(id);
        return ResponseEntity.ok(blog);
    }

    @GetMapping
    public Page<BlogResponse> getPublishedBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return blogService.getPublishedBlogs(pageable);
    }
    
    // Fixed featured blogs implementation
    @GetMapping("/featured")
    public ResponseEntity<List<BlogResponse>> getFeaturedBlogs(
            @RequestParam(defaultValue = "3") int count) {
        Page<BlogResponse> featuredPage = blogService.getPublishedBlogs(
            PageRequest.of(0, count, Sort.by("publishedAt").descending())
        );
        return ResponseEntity.ok(featuredPage.getContent());
    }
}