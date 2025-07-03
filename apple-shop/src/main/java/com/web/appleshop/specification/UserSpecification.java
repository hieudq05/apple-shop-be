package com.web.appleshop.specification;

import com.web.appleshop.dto.request.UserSearchCriteria;
import com.web.appleshop.entity.Role;
import com.web.appleshop.entity.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UserSpecification {
    public static Specification<User> createSpecification(UserSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Objects.requireNonNull(query, "Query must not be null");
            addFetchJoins(root, query, criteria);

            addIdFilter(criteria.getId(), root, cb, predicates);
            addNameFilter(criteria.getName(), root, cb, predicates);
            addEmailFilter(criteria.getEmail(), root, cb, predicates);
            addPhoneFilter(criteria.getPhone(), root, cb, predicates);
            addEnabledFilter(criteria.getEnabled(), root, cb, predicates);
            addRoleFilters(criteria.getRoleName(), root, cb, predicates);
            addDateRangeFilter(criteria, root, cb, predicates);

            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addFetchJoins(Root<User> root, CriteriaQuery<?> query, UserSearchCriteria criteria) {
        // Only add fetch joins for non-count queries
        if (Long.class != query.getResultType()) {
            try {
                // Fetch role (most commonly accessed)
                root.fetch("roles", JoinType.LEFT);
                // Removed or updated fetch call for user_roles
            } catch (Exception e) {
                // In case of any fetch join issues, continue without them
                // This prevents the entire query from failing
            }
        }
    }

    private static void addIdFilter(Integer id, Root<User> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (id != null) {
            predicates.add(cb.equal(root.get("id"), id));
        }
    }

    private static void addNameFilter(String name, Root<User> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(name)) {
            Expression<String> fullName = cb.concat(
                    cb.concat(
                            cb.coalesce(root.get("firstName"), ""),
                            " "
                    ),
                    cb.coalesce(root.get("lastName"), "")
            );

            String searchPattern = "%" + name.toLowerCase() + "%";

            Predicate namePredicate = cb.or(
                    cb.like(cb.lower(root.get("firstName")), searchPattern),
                    cb.like(cb.lower(root.get("lastName")), searchPattern),
                    cb.like(cb.lower(fullName), searchPattern)
            );

            predicates.add(namePredicate);
        }
    }

    private static void addEmailFilter(String email, Root<User> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(email)) {
            predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
        }
    }

    private static void addPhoneFilter(String phone, Root<User> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(phone)) {
            predicates.add(cb.like(root.get("phone"), "%" + phone + "%"));
        }
    }

    private static void addEnabledFilter(Boolean enabled, Root<User> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (enabled != null) {
            predicates.add(cb.equal(root.get("enabled"), enabled));
        }
    }

    private static void addDateRangeFilter(UserSearchCriteria criteria, Root<User> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (criteria.getCreatedAtFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtFrom()));
        }
        if (criteria.getCreatedAtTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtTo()));
        }
        if (criteria.getBirthFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("birth"), criteria.getBirthFrom()));
        }
        if (criteria.getBirthTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("birth"), criteria.getBirthTo()));
        }
    }

    private static void addRoleFilters(Set<String> roles, Root<User> root, CriteriaBuilder cb, List<Predicate> predicates) {
        Join<User, Role> roleJoin = root.join("roles", JoinType.INNER);
        if (roles != null && !roles.isEmpty()) {
            List<Predicate> rolePredicates = new ArrayList<>();
            for (String role : roles) {
                rolePredicates.add(cb.like(cb.lower(roleJoin.get("name")), "%" + role.toLowerCase() + "%"));
            }
            predicates.add(cb.or(rolePredicates.toArray(new Predicate[0])));
        }
    }
}
