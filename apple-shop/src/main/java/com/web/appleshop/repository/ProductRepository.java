package com.web.appleshop.repository;

import com.web.appleshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    Optional<Page<Product>> findAllByCategory_Id(Integer categoryId, Pageable pageable);

    Optional<Product> findProductByIdAndCategory_Id(Integer id, Integer categoryId);

    @Query(value = "SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.createdBy " +
           "LEFT JOIN FETCH p.updatedBy " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.features " +
           "LEFT JOIN FETCH p.stocks s " +
           "LEFT JOIN FETCH s.color " +
           "LEFT JOIN FETCH s.productPhotos",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Product p")
    Page<Product> findAllWithRelationships(Pageable pageable);
}
