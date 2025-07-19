package com.web.appleshop.service;

import com.web.appleshop.dto.projection.BlogForUserInfo;
import com.web.appleshop.dto.projection.BlogInfo;
import com.web.appleshop.dto.projection.BlogSummaryInfo;
import com.web.appleshop.dto.request.CreateBlogRequest;
import com.web.appleshop.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public interface BlogService {
    Page<BlogSummaryInfo> getListBlogsForAdmin(Pageable pageable);

    Blog createBlog(CreateBlogRequest request, MultipartFile fileImage);

    Blog togglePublish(Integer blogId);

    void deleteBlog(Integer blogId);

    Blog updateBlog(Integer blogId, CreateBlogRequest request, MultipartFile fileImage);

    BlogInfo getBlogByIdForAdmin(Integer blogId);

    Page<BlogForUserInfo> getListBlogsForUser(Pageable pageable);

    Long getPublishedBlogCount(LocalDateTime fromDate, LocalDateTime toDate);
}
