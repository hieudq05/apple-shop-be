package com.web.appleshop.specification;

import com.web.appleshop.dto.request.BaseReviewSearchCriteria;
import com.web.appleshop.entity.Review;
import com.web.appleshop.entity.Stock;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseReviewSpecification<T extends BaseReviewSearchCriteria> {

    public Specification<Review> createSpecification(T criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add fetch joins để tránh N+1 query
            assert query != null;
            addFetchJoins(root, query);

            // Common filters
            addCommonFilters(criteria, root, criteriaBuilder, predicates);

            // Specific filters based on implementation
            addSpecificFilters(criteria, root, criteriaBuilder, predicates);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    protected void addFetchJoins(Root<Review> root, CriteriaQuery<?> query) {
        // Kiểm tra nếu không phải count query thì mới add fetch
        if (query.getResultType() != Long.class) {
            // Fetch user
            root.fetch("user", JoinType.LEFT);

            // Fetch product
            Fetch<Review, Stock> stockFetch = root.fetch("stock", JoinType.LEFT);
            stockFetch.fetch("product", JoinType.LEFT);

            // Fetch approvedBy user (có thể null)
            root.fetch("approvedBy", JoinType.LEFT);

            // Fetch repliedBy user (có thể null)
            root.fetch("repliedBy", JoinType.LEFT);
        }
    }

    protected void addCommonFilters(T criteria, Root<Review> root, CriteriaBuilder cb, List<Predicate> predicates) {
        // Tìm kiếm theo nội dung review
        if (StringUtils.hasText(criteria.getContent())) {
            predicates.add(cb.like(
                    cb.lower(root.get("content")),
                    "%" + criteria.getContent().toLowerCase() + "%"
            ));
        }

        // Tìm kiếm theo rating range
        if (criteria.getMinRating() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), criteria.getMinRating()));
        }

        if (criteria.getMaxRating() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("rating"), criteria.getMaxRating()));
        }

        // Tìm kiếm theo khoảng thời gian tạo
        if (criteria.getCreatedAtFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtFrom()));
        }

        if (criteria.getCreatedAtTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtTo()));
        }

        // Tìm kiếm theo tên product
        if (StringUtils.hasText(criteria.getProductName())) {
            addProductNameFilter(criteria.getProductName(), root, cb, predicates);
        }

        // Tìm kiếm theo tên user
        if (StringUtils.hasText(criteria.getUserName())) {
            addUserNameFilter(criteria.getUserName(), root, cb, predicates);
        }

        // Tìm kiếm tổng quát
        if (StringUtils.hasText(criteria.getSearchTerm())) {
            addGeneralSearchFilter(criteria.getSearchTerm(), root, cb, predicates);
        }
    }

    protected abstract void addSpecificFilters(T criteria, Root<Review> root, CriteriaBuilder cb, List<Predicate> predicates);

    private void addProductNameFilter(String productName, Root<Review> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(productName)) {
            Join<Object, Object> productJoin = root.join("product", JoinType.LEFT);
            predicates.add(cb.like(
                    cb.lower(productJoin.get("name")),
                    "%" + productName.toLowerCase() + "%"
            ));
        }
    }

    private void addUserNameFilter(String userName, Root<Review> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(userName)) {
            Join<Object, Object> userJoin = root.join("user", JoinType.LEFT);

            Expression<String> userFullName = cb.concat(
                    cb.concat(
                            cb.coalesce(userJoin.get("firstName"), ""),
                            " "
                    ),
                    cb.coalesce(userJoin.get("lastName"), "")
            );

            String searchPattern = "%" + userName.toLowerCase() + "%";

            Predicate userPredicate = cb.or(
                    cb.like(cb.lower(userJoin.get("firstName")), searchPattern),
                    cb.like(cb.lower(userJoin.get("lastName")), searchPattern),
                    cb.like(cb.lower(userFullName), searchPattern),
                    cb.like(cb.lower(userJoin.get("email")), searchPattern)
            );

            predicates.add(userPredicate);
        }
    }

    private void addGeneralSearchFilter(String searchTerm, Root<Review> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(searchTerm)) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";

            Join<Object, Object> userJoin = root.join("user", JoinType.LEFT);
            Join<Object, Object> productJoin = root.join("product", JoinType.LEFT);

            Expression<String> userFullName = cb.concat(
                    cb.concat(
                            cb.coalesce(userJoin.get("firstName"), ""),
                            " "
                    ),
                    cb.coalesce(userJoin.get("lastName"), "")
            );

            Predicate generalSearchPredicate = cb.or(
                    cb.like(cb.lower(root.get("content")), searchPattern),
                    cb.like(cb.lower(root.get("replyContent")), searchPattern),
                    cb.like(cb.lower(productJoin.get("name")), searchPattern),
                    cb.like(cb.lower(userJoin.get("firstName")), searchPattern),
                    cb.like(cb.lower(userJoin.get("lastName")), searchPattern),
                    cb.like(cb.lower(userFullName), searchPattern),
                    cb.like(cb.lower(userJoin.get("email")), searchPattern)
            );

            predicates.add(generalSearchPredicate);
        }
    }
}
