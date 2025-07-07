package com.web.appleshop.service;

import com.web.appleshop.dto.request.AdminReviewSearchCriteria;
import com.web.appleshop.entity.Review;
import com.web.appleshop.repository.ReviewRepository;
import com.web.appleshop.specification.AdminReviewSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminReviewSearchService {

    private final AdminReviewSpecification adminReviewSpecification;
    private final ReviewRepository reviewRepository;

    public Page<Review> searchReviews(AdminReviewSearchCriteria criteria, Pageable pageable) {
        Specification<Review> spec = createSpecification(criteria);
        return reviewRepository.findAll(spec, pageable);
    }

    public Specification<Review> createSpecification(AdminReviewSearchCriteria criteria) {
        return adminReviewSpecification.createSpecification(criteria);
    }

    // Admin specific methods
    public long countPendingReviews() {
        AdminReviewSearchCriteria criteria = new AdminReviewSearchCriteria();
        criteria.setIsApproved(false);
        return reviewRepository.count(createSpecification(criteria));
    }

    public long countApprovedReviews() {
        AdminReviewSearchCriteria criteria = new AdminReviewSearchCriteria();
        criteria.setIsApproved(true);
        return reviewRepository.count(createSpecification(criteria));
    }

    public Map<String, Long> getReviewStatistics() {
        long totalReviews = reviewRepository.count();
        long pendingReviews = countPendingReviews();
        long approvedReviews = countApprovedReviews();

        return Map.of(
                "total", totalReviews,
                "pending", pendingReviews,
                "approved", approvedReviews
        );
    }
}
