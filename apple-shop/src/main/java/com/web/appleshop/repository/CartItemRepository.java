package com.web.appleshop.repository;

import com.web.appleshop.entity.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> , JpaSpecificationExecutor<CartItem> {
    Optional<CartItem> findCartItemByUserIdAndStock_Id(Integer userId, Integer stockId);

    Optional<CartItem> findCartItemByIdAndUserId(Integer id, Integer userId);

    void deleteAllByUserId(Integer userId);

    Page<CartItem> findCartItemsByUserId(Integer userId, Pageable pageable);

    List<CartItem> findCartItemsByUserId(Integer userId);
}