package com.web.appleshop.repository;

import com.web.appleshop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Integer> , JpaSpecificationExecutor<Review> {

  @Query("select (count(r) > 0) from Review r where r.user.id = ?1 and r.stock.id = ?2 and r.order.id = ?3")
  boolean existsByUserIdAndStockIdAndOrderId(Integer id, Integer id1, Integer id2);
}