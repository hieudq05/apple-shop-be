package com.web.appleshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.web.appleshop.dto.BlogResponse;
import com.web.appleshop.service.BlogService;

public class BlogController {
 @Autowired
    private BlogService blogService;

    @GetMapping
    public Page<BlogResponse> getPublishedBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return blogService.getPublishedBlogs(pageable);
    }
}
