package com.web.appleshop.service;

import com.web.appleshop.entity.Blog;

import java.util.List;
import java.util.Optional;

public interface BlogService {
    Blog create(Blog blog);
    List<Blog> findAll();
    Optional<Blog> findById(Integer id);
    Blog update(Integer id, Blog blog);
    void delete(Integer id);
}

