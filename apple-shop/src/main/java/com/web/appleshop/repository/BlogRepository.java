package com.web.appleshop.repository;

import com.web.appleshop.dto.projection.BlogInfo;
import com.web.appleshop.dto.projection.BlogSummaryInfo;
import com.web.appleshop.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Integer>, JpaSpecificationExecutor<Blog> {
    Page<BlogSummaryInfo> findBlogsBy(Pageable pageable);
    Optional<BlogInfo> findBlogById(Integer id);
}