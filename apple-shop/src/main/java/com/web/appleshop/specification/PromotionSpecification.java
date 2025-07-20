package com.web.appleshop.specification;

import com.web.appleshop.dto.request.PromotionSearchRequest;
import com.web.appleshop.entity.Promotion;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PromotionSpecification {
    public Specification<Promotion> searchPromotions(PromotionSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm kiếm theo keyword (tên hoặc mã)
            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), keyword);
                Predicate codePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("code")), keyword);
                predicates.add(criteriaBuilder.or(namePredicate, codePredicate));
            }

            if (request.getId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("id"), request.getId()));
            }

            // Tìm kiếm chính xác theo mã
            if (StringUtils.hasText(request.getCode())) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("code")),
                        request.getCode().toLowerCase()));
            }

            // Tìm kiếm theo loại giảm giá
            if (request.getPromotionType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("promotionType"), request.getPromotionType()));
            }

            // Tìm kiếm theo trạng thái active
            if (request.getIsActive() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("isActive"), request.getIsActive()));
            }

            // Tìm kiếm theo applyOn
            if (request.getApplyOn() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("applyOn"), request.getApplyOn()));
            }

            // Tìm kiếm theo ngày bắt đầu
            if (request.getStartDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("startDate"), request.getStartDateFrom()));
            }
            if (request.getStartDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("startDate"), request.getStartDateTo()));
            }

            // Tìm kiếm theo ngày kết thúc
            if (request.getEndDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("endDate"), request.getEndDateFrom()));
            }
            if (request.getEndDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("endDate"), request.getEndDateTo()));
            }

            // Tìm kiếm theo giá trị giảm
            if (request.getValueFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("value"), request.getValueFrom()));
            }
            if (request.getValueTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("value"), request.getValueTo()));
            }

            // Tìm kiếm theo giá trị đơn hàng tối thiểu
            if (request.getMinOrderValueFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("minOrderValue"), request.getMinOrderValueFrom()));
            }
            if (request.getMinOrderValueTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("minOrderValue"), request.getMinOrderValueTo()));
            }

            // Tìm kiếm theo giới hạn sử dụng
            if (request.getUsageLimitFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("usageLimit"), request.getUsageLimitFrom()));
            }
            if (request.getUsageLimitTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("usageLimit"), request.getUsageLimitTo()));
            }

            // Tìm kiếm theo số lần đã sử dụng
            if (request.getUsageCountFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("usageCount"), request.getUsageCountFrom()));
            }
            if (request.getUsageCountTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("usageCount"), request.getUsageCountTo()));
            }

            // Distinct để tránh duplicate khi có nhiều joins
            assert query != null;
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
