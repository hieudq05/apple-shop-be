package com.web.appleshop.specification;

import com.web.appleshop.dto.request.CategorySearchCriteria;
import com.web.appleshop.entity.Category;
import com.web.appleshop.entity.Product;
import com.web.appleshop.entity.Promotion;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class CategorySpecification {
    public Specification<Category> createSpecification(CategorySearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add fetch joins để tránh N+1 query
            assert query != null;
            addFetchJoins(root, query);

            query.distinct(true);

            // Add filters
            addFilters(criteria, root, criteriaBuilder, predicates);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addFetchJoins(Root<Category> root, CriteriaQuery<?> query) {
        // Kiểm tra nếu không phải count query thì mới add fetch
        if (query.getResultType() != Long.class) {
            // Fetch products
            root.fetch("products", JoinType.LEFT);

            // Fetch promotions (đã có EAGER fetch nhưng vẫn có thể explicit fetch)
            root.fetch("promotions", JoinType.LEFT);
        }
    }

    private static void addFilters(CategorySearchCriteria criteria, Root<Category> root, CriteriaBuilder cb, List<Predicate> predicates) {
        // Tìm kiếm theo id category
        if (criteria.getId() != null) {
            predicates.add(cb.equal(root.get("id"), criteria.getId()));
        }

        // Tìm kiếm theo tên category
        if (StringUtils.hasText(criteria.getName())) {
            predicates.add(cb.like(
                    cb.lower(root.get("name")),
                    "%" + criteria.getName().toLowerCase() + "%"
            ));
        }

        // Tìm kiếm theo có products hay không
        if (criteria.getHasProducts() != null) {
            if (criteria.getHasProducts()) {
                predicates.add(cb.isNotEmpty(root.get("products")));
            } else {
                predicates.add(cb.isEmpty(root.get("products")));
            }
        }

        // Tìm kiếm theo có promotions hay không
        if (criteria.getHasPromotions() != null) {
            if (criteria.getHasPromotions()) {
                predicates.add(cb.isNotEmpty(root.get("promotions")));
            } else {
                predicates.add(cb.isEmpty(root.get("promotions")));
            }
        }

        // Tìm kiếm theo số lượng products
        if (criteria.getMinProductCount() != null) {
            predicates.add(cb.greaterThanOrEqualTo(cb.size(root.get("products")), criteria.getMinProductCount()));
        }

        if (criteria.getMaxProductCount() != null) {
            predicates.add(cb.lessThanOrEqualTo(cb.size(root.get("products")), criteria.getMaxProductCount()));
        }

        // Tìm kiếm theo tên promotion
        if (StringUtils.hasText(criteria.getPromotionName())) {
            addPromotionNameFilter(criteria.getPromotionName(), root, cb, predicates);
        }

        // Tìm kiếm theo active promotions
        if (criteria.getHasActivePromotions() != null) {
            addActivePromotionsFilter(criteria.getHasActivePromotions(), root, cb, predicates);
        }

        // Tìm kiếm tổng quát
        if (StringUtils.hasText(criteria.getSearchTerm())) {
            addGeneralSearchFilter(criteria.getSearchTerm(), root, cb, predicates);
        }
    }

    private static void addPromotionNameFilter(String promotionName, Root<Category> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(promotionName)) {
            Join<Category, Promotion> promotionJoin = root.join("promotions", JoinType.LEFT);
            predicates.add(cb.like(
                    cb.lower(promotionJoin.get("name")),
                    "%" + promotionName.toLowerCase() + "%"
            ));
        }
    }

    private static void addActivePromotionsFilter(Boolean hasActivePromotions, Root<Category> root, CriteriaBuilder cb, List<Predicate> predicates) {
        Join<Category, Promotion> promotionJoin = root.join("promotions", JoinType.LEFT);

        if (hasActivePromotions) {
            predicates.add(cb.equal(promotionJoin.get("isActive"), true));

            // Hoặc có thể check theo date range nếu có startDate/endDate
            // predicates.add(cb.and(
            //     cb.lessThanOrEqualTo(promotionJoin.get("startDate"), LocalDateTime.now()),
            //     cb.greaterThanOrEqualTo(promotionJoin.get("endDate"), LocalDateTime.now())
            // ));
        } else {
            predicates.add(cb.or(
                    cb.equal(promotionJoin.get("isActive"), false),
                    cb.isNull(promotionJoin.get("id"))
            ));
        }
    }

    private static void addGeneralSearchFilter(String searchTerm, Root<Category> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(searchTerm)) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";

            // Tìm kiếm trong tên category
            Predicate namePredicate = cb.like(cb.lower(root.get("name")), searchPattern);

            // Tìm kiếm trong ảnh category
             Predicate imagePredicate = cb.like(root.get("image"), searchPattern);

            // Tìm kiếm trong tên promotion
            Join<Category, Promotion> promotionJoin = root.join("promotions", JoinType.LEFT);
            Predicate promotionNamePredicate = cb.like(cb.lower(promotionJoin.get("name")), searchPattern);

            // Tìm kiếm trong tên product
            Join<Category, Product> productJoin = root.join("products", JoinType.LEFT);
            Predicate productNamePredicate = cb.like(cb.lower(productJoin.get("name")), searchPattern);

            Predicate generalSearchPredicate = cb.or(
                    namePredicate,
                    promotionNamePredicate,
                    productNamePredicate,
                    imagePredicate
            );

            predicates.add(generalSearchPredicate);
        }
    }

    // Helper methods cho specific searches
    public static Specification<Category> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                StringUtils.hasText(name) ?
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%") :
                        criteriaBuilder.conjunction();
    }

    public static Specification<Category> hasProducts() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNotEmpty(root.get("products"));
    }

    public static Specification<Category> hasPromotions() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNotEmpty(root.get("promotions"));
    }

    public static Specification<Category> hasProductCountBetween(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (min != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.size(root.get("products")), min));
            }

            if (max != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.size(root.get("products")), max));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
