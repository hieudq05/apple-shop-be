package com.web.appleshop.repository;

import com.web.appleshop.dto.projection.CategoryInfoView;
import com.web.appleshop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer>, JpaSpecificationExecutor<Category> {
    Page<CategoryInfoView> findAllBy(Pageable pageable);

    Optional<CategoryInfoView> findCategoryById(Integer id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products ps WHERE ps.isDeleted = false")
    List<Category> findCategoryWith4Products();
}