package com.web.appleshop.service.impl;

import com.web.appleshop.dto.projection.BlogInfo;
import com.web.appleshop.dto.projection.BlogSummaryInfo;
import com.web.appleshop.dto.request.CreateBlogRequest;
import com.web.appleshop.entity.Blog;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.IllegalArgumentException;
import com.web.appleshop.repository.BlogRepository;
import com.web.appleshop.service.BlogService;
import com.web.appleshop.util.UploadUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
class BlogServiceImpl implements BlogService {
    private static final Logger log = LoggerFactory.getLogger(BlogServiceImpl.class);
    private final BlogRepository blogRepository;
    private final UploadUtils uploadUtils;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional(readOnly = true)
    public Page<BlogSummaryInfo> getListBlogsForAdmin(Pageable pageable) {
        return blogRepository.findBlogsBy(pageable);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Blog createBlog(CreateBlogRequest request, MultipartFile fileImage) {
        log.info("file image: {}", fileImage);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Blog blog = new Blog();
        BeanUtils.copyProperties(request, blog);
        blog.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        blog.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        blog.setAuthor(user);
        blog.setThumbnail(!fileImage.isEmpty() ? uploadUtils.uploadFile(fileImage) : request.getThumbnail());

        return blogRepository.save(blog);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Blog togglePublish(Integer blogId) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy bài viết.")
        );
        if (blog.getIsPublished()) {
            blog.setPublishedAt(null);
        } else {
            blog.setPublishedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        }
        blog.setIsPublished(!blog.getIsPublished());
        return blogRepository.save(blog);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public void deleteBlog(Integer blogId) {
        blogRepository.deleteById(blogId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Blog updateBlog(Integer blogId, CreateBlogRequest request, MultipartFile fileImage) {
        log.info("file image: {}", fileImage);
        Blog blog = blogRepository.findById(blogId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy bài viết.")
        );
        BeanUtils.copyProperties(request, blog);
        blog.setThumbnail(!fileImage.isEmpty() ? uploadUtils.uploadFile(fileImage) : request.getThumbnail());
        blog.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        return blogRepository.save(blog);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional(readOnly = true)
    public BlogInfo getBlogByIdForAdmin(Integer blogId) {
        return blogRepository.findBlogById(blogId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy bài viết.")
        );
    }
}
