package com.web.appleshop.repository;

import com.web.appleshop.dto.projection.UserAdminSummaryInfo;
import com.web.appleshop.dto.projection.UserInfo;
import com.web.appleshop.dto.response.admin.UserAdminInfoDto;
import com.web.appleshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> getUserByEmail(String username);

    Optional<User> findUserByEmail(String email);

    Collection<Object> findAllByIdIn(Collection<Integer> ids);

    Optional<UserInfo> findUserByEnabledAndId(Boolean enabled, Integer id);

    Page<UserAdminSummaryInfo> findUsersBy(Pageable pageable);

    @Query("SELECT new com.web.appleshop.dto.response.admin.UserAdminInfoDto(" +
            "u.id, u.email, u.phone, u.firstName, u.lastName, " +
            "u.image, u.createdAt, u.enabled, u.birth, " +
            "(SELECT new com.web.appleshop.dto.response.admin.UserAdminInfoDto.RoleDto(r.id, r.name) FROM u.roles r)) " +
            "FROM User u LEFT JOIN FETCH u.roles " +
            "WHERE u.id = :id")
    UserAdminInfoDto findUserById(Integer id);
}