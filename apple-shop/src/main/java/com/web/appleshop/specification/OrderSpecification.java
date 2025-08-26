package com.web.appleshop.specification;

import com.web.appleshop.dto.request.AdminOrderSearchCriteria;
import com.web.appleshop.dto.request.BaseOrderSearchCriteria;
import com.web.appleshop.dto.request.UserOrderSearchCriteria;
import com.web.appleshop.entity.Order;
import com.web.appleshop.entity.User;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderSpecification {
    private static final Logger log = LoggerFactory.getLogger(OrderSpecification.class);

    public <T extends BaseOrderSearchCriteria> Specification<Order> buildSpecification(T criteria) {
        Specification<Order> spec = null;

        if (criteria instanceof AdminOrderSearchCriteria adminCriteria) {
            spec = createSpecification(adminCriteria);
        } else if (criteria instanceof UserOrderSearchCriteria userCriteria) {
            spec = createSpecification(userCriteria);
        }

        return spec;
    }

    public Specification<Order> createSpecification(BaseOrderSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            assert query != null;
            addFetchJoins(root, query);

            // Common filters
            addCommonFilters(criteria, root, criteriaBuilder, predicates);

            if (StringUtils.hasText(criteria.getSortBy())) {
                System.out.println("Sorting by: " + criteria.getSortBy());
                System.out.println("Sorting direction: " + criteria.getSortDirection());
                switch (criteria.getSortBy().toLowerCase()) {
                    case "createdat":
                        Path<?> createdAtPath = root.get("createdAt");
                        if("desc".equalsIgnoreCase(criteria.getSortDirection())) {
                            query.orderBy(criteriaBuilder.desc(createdAtPath));
                        } else {
                            query.orderBy(criteriaBuilder.asc(createdAtPath));
                        }
                        break;
                    case "approveat":
                        Path<?> approveAtPath = root.get("approveAt");
                        if("desc".equalsIgnoreCase(criteria.getSortDirection())) {
                            query.orderBy(criteriaBuilder.desc(approveAtPath));
                        } else {
                            query.orderBy(criteriaBuilder.asc(approveAtPath));
                        }
                        break;
                    case "total":
                        Path<?> totalPath = root.get("finalTotal");
                        if("desc".equalsIgnoreCase(criteria.getSortDirection())) {
                            query.orderBy(criteriaBuilder.desc(totalPath));
                        } else {
                            query.orderBy(criteriaBuilder.asc(totalPath));
                        }
                        break;
                    case "status":
                        Path<?> statusPath = root.get("status");
                        if("desc".equalsIgnoreCase(criteria.getSortDirection())) {
                            query.orderBy(criteriaBuilder.desc(statusPath));
                        } else {
                            query.orderBy(criteriaBuilder.asc(statusPath));
                        }
                        break;
                    case "paymenttype":
                        Path<?> paymentTypePath = root.get("paymentType");
                        if("desc".equalsIgnoreCase(criteria.getSortDirection())) {
                            query.orderBy(criteriaBuilder.desc(paymentTypePath));
                        } else {
                            query.orderBy(criteriaBuilder.asc(paymentTypePath));
                        }
                        break;
                    default:
                        try {
                            Path<?> directPath = root.get(criteria.getSortBy());
                            query.orderBy(criteriaBuilder.asc(directPath));
                        } catch (IllegalArgumentException e) {
                            log.error("Invalid sort field: {}", criteria.getSortBy());
                        }
                        break;
                }
            }

            // Admin filters
            if (criteria instanceof AdminOrderSearchCriteria adminCriteria) {
                addSpecificFilters(adminCriteria, root, criteriaBuilder, predicates);
            } else if (criteria instanceof UserOrderSearchCriteria userCriteria) {
                addUserFilters(userCriteria, root, criteriaBuilder, predicates);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    protected void addCommonFilters(BaseOrderSearchCriteria criteria, Root<Order> root, CriteriaBuilder cb, List<Predicate> predicates) {
        // Tìm kiếm theo tên khách hàng (firstName + lastName)
        if (StringUtils.hasText(criteria.getCustomerName())) {
            addCustomerNameFilter(criteria.getCustomerName(), root, cb, predicates);
        }

        // Tìm kiếm theo trạng thái đơn hàng
        if (criteria.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
        }

        // Tìm kiếm theo email khách hàng
        if (StringUtils.hasText(criteria.getCustomerEmail())) {
            predicates.add(cb.like(
                    cb.lower(root.get("email")),
                    "%" + criteria.getCustomerEmail().toLowerCase() + "%"
            ));
        }

        // Tìm kiếm theo phone khách hàng
        if (StringUtils.hasText(criteria.getCustomerPhone())) {
            predicates.add(cb.like(
                    root.get("phone"),
                    "%" + criteria.getCustomerPhone() + "%"
            ));
        }

        // Tìm kiếm theo địa chỉ giao hàng
        if (StringUtils.hasText(criteria.getShippingAddress())) {
            predicates.add(cb.like(
                    cb.lower(root.get("address")),
                    "%" + criteria.getShippingAddress().toLowerCase() + "%"
            ));
        }

        // Tìm kiếm theo tỉnh/thành phố
        if (StringUtils.hasText(criteria.getProvince())) {
            predicates.add(cb.like(
                    cb.lower(root.get("province")),
                    "%" + criteria.getProvince().toLowerCase() + "%"
            ));
        }

        // Tìm kiếm theo quận/huyện
        if (StringUtils.hasText(criteria.getDistrict())) {
            predicates.add(cb.like(
                    cb.lower(root.get("district")),
                    "%" + criteria.getDistrict().toLowerCase() + "%"
            ));
        }

        // Tìm kiếm theo phường/xã
        if (StringUtils.hasText(criteria.getWard())) {
            predicates.add(cb.like(
                    cb.lower(root.get("ward")),
                    "%" + criteria.getWard().toLowerCase() + "%"
            ));
        }

        // Tìm kiếm theo quốc gia
        if (StringUtils.hasText(criteria.getCountry())) {
            predicates.add(cb.like(
                    cb.lower(root.get("country")),
                    "%" + criteria.getCountry().toLowerCase() + "%"
            ));
        }

        // Tìm kiếm theo mã tracking
        if (StringUtils.hasText(criteria.getShippingTrackingCode())) {
            predicates.add(cb.like(
                    cb.lower(root.get("shippingTrackingCode")),
                    "%" + criteria.getShippingTrackingCode().toLowerCase() + "%"
            ));
        }

        // Tìm kiếm theo khoảng thời gian tạo đơn hàng
        if (criteria.getCreatedAtFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtFrom()));
        }
        if (criteria.getCreatedAtTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtTo()));
        }

        // Tìm kiếm tổng quát
        if (StringUtils.hasText(criteria.getSearchTerm())) {
            addGeneralSearchFilter(criteria.getSearchTerm(), root, cb, predicates);
        }
    }

    private void addCustomerNameFilter(String customerName, Root<Order> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(customerName)) {
            Expression<String> fullName = cb.concat(
                    cb.concat(
                            cb.coalesce(root.get("firstName"), ""),
                            " "
                    ),
                    cb.coalesce(root.get("lastName"), "")
            );

            String searchPattern = "%" + customerName.toLowerCase() + "%";

            Predicate namePredicate = cb.or(
                    cb.like(cb.lower(root.get("firstName")), searchPattern),
                    cb.like(cb.lower(root.get("lastName")), searchPattern),
                    cb.like(cb.lower(fullName), searchPattern)
            );

            predicates.add(namePredicate);
        }
    }

    private void addGeneralSearchFilter(String searchTerm, Root<Order> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (StringUtils.hasText(searchTerm)) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";

            Expression<String> customerFullName = cb.concat(
                    cb.concat(
                            cb.coalesce(root.get("firstName"), ""),
                            " "
                    ),
                    cb.coalesce(root.get("lastName"), "")
            );

            Predicate generalSearchPredicate = cb.or(
                    cb.like(cb.lower(root.get("email")), searchPattern),
                    cb.like(root.get("phone"), searchPattern),
                    cb.like(cb.lower(root.get("firstName")), searchPattern),
                    cb.like(cb.lower(root.get("lastName")), searchPattern),
                    cb.like(cb.lower(customerFullName), searchPattern),
                    cb.like(cb.lower(root.get("address")), searchPattern),
                    cb.like(cb.lower(root.get("shippingTrackingCode")), searchPattern)
            );

            predicates.add(generalSearchPredicate);
        }
    }

    private void addSpecificFilters(AdminOrderSearchCriteria criteria, Root<Order> root, CriteriaBuilder cb, List<Predicate> predicates) {
        // Filters tìm kiếm cho admin
        if (criteria.getPaymentType() != null) {
            predicates.add(cb.equal(root.get("paymentType"), criteria.getPaymentType()));
        }
        if (criteria.getApproveAtFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("approveAt"), criteria.getApproveAtFrom()));
        }
        if (criteria.getApproveAtTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("approveAt"), criteria.getApproveAtTo()));
        }
        if (criteria.getCreatedById() != null) {
            predicates.add(cb.equal(root.get("createdBy").get("id"), criteria.getCreatedById()));
        }
        if (criteria.getCreatedByName() != null) {
            predicates.add(cb.equal(root.get("createdBy").get("firstName"), criteria.getCreatedByName()));
        }
        if (criteria.getApprovedById() != null) {
            predicates.add(cb.equal(root.get("approvedBy").get("id"), criteria.getApprovedById()));
        }
        if (criteria.getApprovedByName() != null) {
            predicates.add(cb.equal(root.get("approvedBy").get("firstName"), criteria.getApprovedByName()));
        }
    }

    private void addUserFilters(UserOrderSearchCriteria criteria, Root<Order> root, CriteriaBuilder cb, List<Predicate> predicates) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        predicates.add(cb.equal(root.get("createdBy").get("id"), user.getId()));
    }

    protected void addFetchJoins(Root<Order> root, CriteriaQuery<?> query) {
        // Kiểm tra nếu không phải count query thì mới add fetch
        if (query.getResultType() != Long.class) {
            // Fetch createdBy user
            Fetch<Order, User> userFetch = root.fetch("createdBy", JoinType.LEFT);
            userFetch.fetch("roles", JoinType.LEFT);

            // Fetch approveBy user (có thể null)
            Fetch<Order, User> approvedByFetch = root.fetch("approveBy", JoinType.LEFT);
            approvedByFetch.fetch("roles", JoinType.LEFT);

            // Fetch order details
            root.fetch("orderDetails", JoinType.LEFT);
        }
    }
}
