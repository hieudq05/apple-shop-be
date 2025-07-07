package com.web.appleshop.repository;

import com.web.appleshop.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> , JpaSpecificationExecutor<Promotion> {
    @Query("select (count(p) > 0) from Promotion p where p.code = ?1")
    boolean existsByCode(String code);

    @Query("SELECT p FROM Promotion p " +
            "WHERE p.code = :code " +
            "AND p.isActive = true " +
            "AND p.startDate <= :now " +
            "AND p.endDate >= :now " +
            "AND p.usageCount < p.usageLimit")
    Optional<Promotion> findValidPromotionByCode(@Param("code") String code,
                                                 @Param("now") LocalDateTime now);
}