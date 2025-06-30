package com.web.appleshop.service;

import com.web.appleshop.dto.projection.BlogSummaryInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogService {
    Page<BlogSummaryInfo> getListBlogsForAdmin(Pageable pageable);
}
