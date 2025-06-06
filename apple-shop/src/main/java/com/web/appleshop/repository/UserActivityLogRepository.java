package com.web.appleshop.repository;

import com.web.appleshop.entity.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> , JpaSpecificationExecutor<UserActivityLog> {
  }