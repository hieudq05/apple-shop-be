package com.web.appleshop.service;

import com.web.appleshop.dto.BlogResponse;
import com.web.appleshop.entity.Blog;
import com.web.appleshop.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
public class BlogService {
 @Autowired
    private BlogRepository blogRepository;

    public Page<BlogResponse> getPublishedBlogs(Pageable pageable) {
        Page<Blog> publishedBlogs = blogRepository.findByStatus("Published", pageable);
        return publishedBlogs.map(this::convertToResponse);
    }

    private BlogResponse convertToResponse(Blog blog) {
        BlogResponse dto = new BlogResponse();
        dto.setId(blog.getId());
        dto.setTitle(blog.getTitle());
        dto.setThumbnail(blog.getThumbnail());
        dto.setPublishedAt(blog.getPublishedAt());

        if (blog.getAuthor() != null) {
            try {
                dto.setAuthorName(blog.getAuthor().getFullName());
            } catch (Exception e) {
                dto.setAuthorName("Tác giả không rõ");
            }
        } else {
            dto.setAuthorName("Tác giả không rõ");
        }

        return dto;
    }
}
