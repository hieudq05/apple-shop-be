package com.web.appleshop.repository;

import com.web.appleshop.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> , JpaSpecificationExecutor<Promotion> {
    @Query("select (count(p) > 0) from Promotion p where p.code = ?1")
    boolean existsByCode(String code);
}