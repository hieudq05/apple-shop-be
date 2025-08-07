package com.web.appleshop.repository;

import com.web.appleshop.entity.SavedProduct;
import com.web.appleshop.entity.SavedProductId;
import com.web.appleshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SavedProductRepository extends JpaRepository<SavedProduct, SavedProductId> , JpaSpecificationExecutor<SavedProduct> {
    void deleteSavedProductByUserIdAndStockId(Integer userId, Integer stockId);

    Page<SavedProduct> findSavedProductsByUserAndProduct_IsDeleted(User user, Boolean productIsDeleted, Pageable pageable);
}