package com.web.appleshop.repository;

import com.web.appleshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> , JpaSpecificationExecutor<User> {

    Optional<User> getUserByEmail(String username);

    Optional<User> findUserByEmail(String email);
}