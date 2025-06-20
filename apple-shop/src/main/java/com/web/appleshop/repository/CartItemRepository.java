package com.web.appleshop.repository;

import com.web.appleshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> , JpaSpecificationExecutor<CartItem> {
    Optional<CartItem> findCartItemByUserIdAndStock_Id(Integer userId, Integer stockId);

    Optional<CartItem> findCartItemById(Integer id);

    void deleteCartItemById(Integer id);
}