package com.web.appleshop.service.impl;

import com.web.appleshop.entity.Blog;
import com.web.appleshop.entity.User;
import com.web.appleshop.repository.BlogRepository;
import com.web.appleshop.repository.UserRepository;
import com.web.appleshop.dto.request.BlogRequest;
import com.web.appleshop.service.BlogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    @Override
    public Blog create(BlogRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả"));

        Blog blog = new Blog();
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setThumbnail(request.getThumbnail());
        blog.setStatus(request.getStatus());
        blog.setAuthor(author);
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());

        if ("PUBLISHED".equalsIgnoreCase(request.getStatus())) {
            blog.setPublishedAt(LocalDateTime.now());
        }

        return blogRepository.save(blog);
    }

    @Override
    public Page<Blog> findAllPaginated(int page, int size) {
        return blogRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Optional<Blog> findById(Integer id) {
        return blogRepository.findById(id);
    }

    @Override
    @Transactional
    public Blog update(Integer id, BlogRequest request) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy blog"));

        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setThumbnail(request.getThumbnail());
        blog.setStatus(request.getStatus());
        blog.setUpdatedAt(LocalDateTime.now());

        if ("PUBLISHED".equalsIgnoreCase(request.getStatus()) && blog.getPublishedAt() == null) {
            blog.setPublishedAt(LocalDateTime.now());
        }

        return blogRepository.save(blog);
    }

    @Override
    public void delete(Integer id) {
        blogRepository.deleteById(id);
    }
}

