package com.web.appleshop.specification;

import com.web.appleshop.dto.request.UserReviewSearchCriteria;
import com.web.appleshop.entity.Review;
import com.web.appleshop.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@AllArgsConstructor
public class UserReviewSpecification extends BaseReviewSpecification<UserReviewSearchCriteria> {
    private User currentUser;

    @Override
    protected void addSpecificFilters(UserReviewSearchCriteria criteria, Root<Review> root, CriteriaBuilder cb, List<Predicate> predicates) {
        // LUÔN LUÔN áp dụng filter: User chỉ thấy review đã approved HOẶC review của chính mình
        addUserVisibilityFilter(root, cb, predicates);

        // Các filter khác chỉ áp dụng sau khi đã filter visibility

        // Tìm kiếm theo trạng thái approved (chỉ khi user muốn filter thêm)
        if (criteria.getIsApproved() != null) {
            predicates.add(cb.equal(root.get("isApproved"), criteria.getIsApproved()));
        }

        // Tìm kiếm theo có reply hay không
        if (criteria.getHasReply() != null) {
            if (criteria.getHasReply()) {
                predicates.add(cb.isNotNull(root.get("replyContent")));
            } else {
                predicates.add(cb.isNull(root.get("replyContent")));
            }
        }

        // Tìm kiếm theo stock ID
        if (criteria.getProductId() != null) {
            predicates.add(cb.equal(root.get("stock").get("product").get("id"), criteria.getProductId()));
        }

        if (criteria.getStockId() != null) {
            predicates.add(cb.equal(root.get("stock").get("id"), criteria.getStockId()));
        }
    }

    private void addUserVisibilityFilter(Root<Review> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (currentUser != null) {
            // User đã login: xem review đã approved HOẶC review của chính mình
            Predicate approvedReviewPredicate = cb.equal(root.get("isApproved"), true);
            Predicate ownReviewPredicate = cb.equal(root.get("user"), currentUser);

            predicates.add(cb.or(approvedReviewPredicate, ownReviewPredicate));
        } else {
            // User chưa login: chỉ xem review đã approved
            predicates.add(cb.equal(root.get("isApproved"), true));
        }
    }

    // Factory method để tạo specification cho user đã login
    public static UserReviewSpecification forUser(User user) {
        return new UserReviewSpecification(user);
    }

    // Factory method để tạo specification cho guest user (chưa login)
    public static UserReviewSpecification forGuest() {
        return new UserReviewSpecification(null);
    }
}
