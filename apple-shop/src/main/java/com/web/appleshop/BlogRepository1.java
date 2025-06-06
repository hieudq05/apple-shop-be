package com.web.appleshop;

import com.web.appleshop.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BlogRepository1 extends JpaRepository<Blog, Integer>, JpaSpecificationExecutor<Blog> {
}