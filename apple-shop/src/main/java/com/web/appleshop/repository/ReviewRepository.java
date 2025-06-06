package com.web.appleshop.repository;

import com.web.appleshop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReviewRepository extends JpaRepository<Review, Integer> , JpaSpecificationExecutor<Review> {
  }