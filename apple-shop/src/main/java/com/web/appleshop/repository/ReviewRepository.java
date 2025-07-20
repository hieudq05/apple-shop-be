package com.web.appleshop.repository;

import com.web.appleshop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReviewRepository extends JpaRepository<Review, Integer> , JpaSpecificationExecutor<Review> {
  @Query("select (count(r) > 0) from Review r where r.user.id = ?1 and r.stock.id = ?2 and r.order.id = ?3")
  boolean existsByUserIdAndStockIdAndOrderId(Integer id, Integer id1, Integer id2);

  @Query("SELECT COUNT(r) FROM Review r WHERE r.createdAt BETWEEN :startDate AND :endDate")
  Long getReviewCount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

  @Query("SELECT AVG(r.rating) FROM Review r JOIN r.stock s WHERE s.product.id = :productId")
  Double getAverageRatingByProduct(@Param("productId") Integer productId);
}