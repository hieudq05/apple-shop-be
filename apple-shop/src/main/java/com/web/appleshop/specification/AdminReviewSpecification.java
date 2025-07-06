package com.web.appleshop.specification;

import com.web.appleshop.dto.request.AdminReviewSearchCriteria;
import com.web.appleshop.entity.Review;
import com.web.appleshop.entity.User;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class AdminReviewSpecification extends BaseReviewSpecification<AdminReviewSearchCriteria> {
    @Override
    protected void addFetchJoins(Root<Review> root, CriteriaQuery<?> query) {
        // Gọi parent fetch joins
        super.addFetchJoins(root, query);

        // Admin có thể cần thêm fetch cho product category
        if (query.getResultType() != Long.class) {
            try {
                root.fetch("product", JoinType.LEFT).fetch("category", JoinType.LEFT);
            } catch (IllegalArgumentException e) {
                // Ignore if already fetched
            }
        }
    }

    @Override
    protected void addSpecificFilters(AdminReviewSearchCriteria criteria, Root<Review> root, CriteriaBuilder cb, List<Predicate> predicates) {
        // Tìm kiếm theo trạng thái approved
        if (criteria.getIsApproved() != null) {
            predicates.add(cb.equal(root.get("isApproved"), criteria.getIsApproved()));
        }

        // Tìm kiếm theo người approve
        if (StringUtils.hasText(criteria.getApprovedByName())) {
            addApprovedByFilter(criteria.getApprovedByName(), root, cb, predicates);
        }

        // Tìm kiếm theo khoảng thời gian approve
        if (criteria.getApprovedAtFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("approvedAt"), criteria.getApprovedAtFrom()));
        }

        if (criteria.getApprovedAtTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("approvedAt"), criteria.getApprovedAtTo()));
        }

        // Tìm kiếm theo nội dung reply
        if (StringUtils.hasText(criteria.getReplyContent())) {
            predicates.add(cb.like(
                    cb.lower(root.get("replyContent")),
                    "%" + criteria.getReplyContent().toLowerCase() + "%"
            ));
        }

        // Tìm kiếm theo người reply
        if (StringUtils.hasText(criteria.getRepliedByName())) {
            addRepliedByFilter(criteria.getRepliedByName(), root, cb, predicates);
        }

        // Tìm kiếm theo có reply hay không
        if (criteria.getHasReply() != null) {
            if (criteria.getHasReply()) {
                predicates.add(cb.isNotNull(root.get("replyContent")));
            } else {
                predicates.add(cb.isNull(root.get("replyContent")));
            }
        }

        // Tìm kiếm theo user ID
        if (criteria.getUserId() != null) {
            predicates.add(cb.equal(root.get("user").get("id"), criteria.getUserId()));
        }

        // Tìm kiếm theo product ID
        if (criteria.getProductId() != null) {
            predicates.add(cb.equal(root.get("product").get("id"), criteria.getProductId()));
        }

        // Tìm kiếm theo category name
        if (StringUtils.hasText(criteria.getCategoryName())) {
            addCategoryNameFilter(criteria.getCategoryName(), root, cb, predicates);
        }
    }

    private void addApprovedByFilter(String approvedByName, Root<Review> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(approvedByName)) {
            Join<Review, User> approvedByJoin = root.join("approvedBy", JoinType.LEFT);

            Expression<String> approvedByFullName = cb.concat(
                    cb.concat(
                            cb.coalesce(approvedByJoin.get("firstName"), ""),
                            " "
                    ),
                    cb.coalesce(approvedByJoin.get("lastName"), "")
            );

            String searchPattern = "%" + approvedByName.toLowerCase() + "%";

            Predicate approvedByPredicate = cb.or(
                    cb.like(cb.lower(approvedByJoin.get("firstName")), searchPattern),
                    cb.like(cb.lower(approvedByJoin.get("lastName")), searchPattern),
                    cb.like(cb.lower(approvedByFullName), searchPattern),
                    cb.like(cb.lower(approvedByJoin.get("email")), searchPattern)
            );

            predicates.add(approvedByPredicate);
        }
    }

    private void addRepliedByFilter(String repliedByName, Root<Review> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(repliedByName)) {
            Join<Review, User> repliedByJoin = root.join("repliedBy", JoinType.LEFT);

            Expression<String> repliedByFullName = cb.concat(
                    cb.concat(
                            cb.coalesce(repliedByJoin.get("firstName"), ""),
                            " "
                    ),
                    cb.coalesce(repliedByJoin.get("lastName"), "")
            );

            String searchPattern = "%" + repliedByName.toLowerCase() + "%";

            Predicate repliedByPredicate = cb.or(
                    cb.like(cb.lower(repliedByJoin.get("firstName")), searchPattern),
                    cb.like(cb.lower(repliedByJoin.get("lastName")), searchPattern),
                    cb.like(cb.lower(repliedByFullName), searchPattern),
                    cb.like(cb.lower(repliedByJoin.get("email")), searchPattern)
            );

            predicates.add(repliedByPredicate);
        }
    }

    private void addCategoryNameFilter(String categoryName, Root<Review> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(categoryName)) {
            Join<Object, Object> productJoin = root.join("product", JoinType.LEFT);
            Join<Object, Object> categoryJoin = productJoin.join("category", JoinType.LEFT);

            predicates.add(cb.like(
                    cb.lower(categoryJoin.get("name")),
                    "%" + categoryName.toLowerCase() + "%"
            ));
        }
    }
}
