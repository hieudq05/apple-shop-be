package com.web.appleshop.repository;

import com.web.appleshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    Optional<Page<Product>> findAllByCategory_Id(Integer categoryId, Pageable pageable);

    Optional<Product> findProductByIdAndCategory_Id(Integer id, Integer categoryId);
}