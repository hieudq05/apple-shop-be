package com.web.appleshop.repository;

import com.web.appleshop.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

public interface BlogRepository extends JpaRepository<Blog, Integer> , JpaSpecificationExecutor<Blog> {
  }