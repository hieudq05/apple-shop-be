package com.web.appleshop.repository;

import com.web.appleshop.dto.projection.BlogForUserInfo;
import com.web.appleshop.dto.projection.BlogInfo;
import com.web.appleshop.dto.projection.BlogSummaryInfo;
import com.web.appleshop.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Integer>, JpaSpecificationExecutor<Blog> {
    Page<BlogSummaryInfo> findBlogsBy(Pageable pageable);

    Optional<BlogInfo> findBlogById(Integer id);

    Page<BlogForUserInfo> findBlogsByIsPublished(Boolean isPublished, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Blog b WHERE b.isPublished = true AND b.publishedAt BETWEEN :startDate AND :endDate")
    Long getPublishedBlogsCount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}