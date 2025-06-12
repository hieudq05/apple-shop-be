package com.web.appleshop.repository;

import com.web.appleshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> , JpaSpecificationExecutor<User> {
    Optional<UserDetails> findByUsernameOrEmailOrPhone(String username, String email, String phone);

    Optional<User> getUserByUsernameOrEmailOrPhone(String username, String email, String phone);

    Optional<User> findByUsername(String username);
}