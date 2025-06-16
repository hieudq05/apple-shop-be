package com.web.appleshop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.web.appleshop.entity.Blog;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
    Page<Blog> findByStatus(String status, Pageable pageable);
}
