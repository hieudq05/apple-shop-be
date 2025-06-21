package com.web.appleshop.service.impl;

import com.web.appleshop.entity.Blog;
import com.web.appleshop.repository.BlogRepository;
import com.web.appleshop.service.BlogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    @Override
    public Blog create(Blog blog) {
        blog.setCreatedAt(LocalDateTime.now());
        return blogRepository.save(blog);
    }

    @Override
    public List<Blog> findAll() {
        return blogRepository.findAll();
    }

    @Override
    public Optional<Blog> findById(Integer id) {
        return blogRepository.findById(id);
    }

    @Override
    @Transactional
    public Blog update(Integer id, Blog updatedBlog) {
        return blogRepository.findById(id)
                .map(blog -> {
                    blog.setTitle(updatedBlog.getTitle());
                    blog.setContent(updatedBlog.getContent());
                    blog.setThumbnail(updatedBlog.getThumbnail());
                    blog.setStatus(updatedBlog.getStatus());
                    blog.setPublishedAt(updatedBlog.getPublishedAt());
                    blog.setUpdatedAt(LocalDateTime.now());
                    return blogRepository.save(blog);
                }).orElseThrow(() -> new RuntimeException("Blog not found"));
    }

    @Override
    public void delete(Integer id) {
        blogRepository.deleteById(id);
    }
}
