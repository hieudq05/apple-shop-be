package com.web.appleshop.service;

import com.web.appleshop.dto.BlogResponse;
import com.web.appleshop.entity.Blog;
import com.web.appleshop.exception.ResourceNotFoundException;
import com.web.appleshop.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    @Autowired
    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Transactional(readOnly = true)
    public Page<BlogResponse> getPublishedBlogs(Pageable pageable) {
        return blogRepository.findByStatus("Published", pageable)
                .map(this::convertToSummaryResponse);
    }

    @Transactional(readOnly = true)
    public BlogResponse getBlogById(Integer id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));
        
        return convertToFullResponse(blog);
    }

    // For list view (summary)
    private BlogResponse convertToSummaryResponse(Blog blog) {
        BlogResponse dto = new BlogResponse();
        dto.setId(blog.getId());
        dto.setTitle(blog.getTitle());
        dto.setThumbnail(blog.getThumbnail());
        dto.setPublishedAt(blog.getPublishedAt());
        dto.setAuthorName(getAuthorName(blog));
        return dto;
    }

    // For detail view (full)
    private BlogResponse convertToFullResponse(Blog blog) {
        BlogResponse dto = convertToSummaryResponse(blog);  // Reuse common fields
        
        // Add detail-specific fields
        dto.setContent(blog.getContent());
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setUpdatedAt(blog.getUpdatedAt());
        dto.setStatus(blog.getStatus());
        
        return dto;
    }

    private String getAuthorName(Blog blog) {
        if (blog.getAuthor() != null && blog.getAuthor().getFullName() != null) {
            return blog.getAuthor().getFullName();
        }
        return "Tác giả không rõ";
    }
}