package com.web.appleshop.repository;

import com.web.appleshop.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoleRepository extends JpaRepository<Role, Integer> , JpaSpecificationExecutor<Role> {
  }