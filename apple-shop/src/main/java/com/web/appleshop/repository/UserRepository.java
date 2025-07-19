package com.web.appleshop.repository;

import com.web.appleshop.dto.projection.UserAdminSummaryInfo;
import com.web.appleshop.dto.projection.UserInfo;
import com.web.appleshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> getUserByEmail(String username);

    Optional<User> findUserByEmail(String email);

    Collection<Object> findAllByIdIn(Collection<Integer> ids);

    Optional<UserInfo> findUserByEnabledAndId(Boolean enabled, Integer id);

    Page<UserAdminSummaryInfo> findUsersBy(Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findUserWithRolesById(@Param("id") Integer id);

    /**
     * Statistics
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :fromDate AND :toDate")
    Long getNewUsersCount(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    @Query("""
            SELECT new map (o.createdBy.id as userId, o.createdBy.firstName as firstName, o.createdBy.lastName as lastName, SUM(o.finalTotal) AS totalSpent) FROM Order o
            WHERE o.createdAt BETWEEN :fromDate AND :toDate AND o.status = 'DELIVERED'
            GROUP BY o.createdBy.id, o.createdBy.firstName, o.createdBy.lastName
            ORDER BY SUM(o.finalTotal) DESC
            """)
    Page<Map<String, Object>> getTopCustomersByPrice(Pageable pageable, @Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    @Query("""
            SELECT new map (o.createdBy.id as userId, o.createdBy.firstName as firstName, o.createdBy.lastName as lastName, COUNT(o) AS orderCount) FROM Order o
            WHERE o.createdAt BETWEEN :fromDate AND :toDate AND o.status = 'DELIVERED'
            GROUP BY o.createdBy.id, o.createdBy.firstName, o.createdBy.lastName
            ORDER BY COUNT(o) DESC
            """)
    Page<Map<String, Object>> getTopCustomersByOrderCount(Pageable pageable, @Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}