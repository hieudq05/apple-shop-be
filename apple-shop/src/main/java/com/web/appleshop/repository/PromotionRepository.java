package com.web.appleshop.repository;

import com.web.appleshop.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Integer>, JpaSpecificationExecutor<Promotion> {
    @Query("select (count(p) > 0) from Promotion p where p.code = ?1")
    boolean existsByCode(String code);

    Optional<Promotion> findByCodeAndIsActive(String code, Boolean isActive);

    Page<Promotion> findPromotionsByIsActive(Boolean isActive, Pageable pageable);
}