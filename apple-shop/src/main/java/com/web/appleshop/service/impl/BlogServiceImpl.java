package com.web.appleshop.service.impl;

import com.web.appleshop.dto.projection.BlogSummaryInfo;
import com.web.appleshop.repository.BlogRepository;
import com.web.appleshop.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class BlogServiceImpl implements BlogService {
    private final BlogRepository blogRepository;

    @Override
    public Page<BlogSummaryInfo> getListBlogsForAdmin(Pageable pageable) {
        return blogRepository.findBlogsBy(pageable);
    }
}
