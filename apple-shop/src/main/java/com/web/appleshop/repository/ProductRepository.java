package com.web.appleshop.repository;

import com.web.appleshop.dto.response.admin.FeatureSummaryDto;
import com.web.appleshop.dto.response.admin.ProductAdminListDto;
import com.web.appleshop.dto.response.admin.ProductFeatureDto;
import com.web.appleshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.*;

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

    @Query("""
            SELECT new com.web.appleshop.dto.response.admin.ProductAdminListDto(
            p.id, p.name, p.description, p.createdAt, CONCAT(p.createdBy.firstName, ' ', p.createdBy.lastName), p.category.id, p.category.name, p.isDeleted)
                        FROM Product p
            """)
    Page<ProductAdminListDto> findProductAdminList(Pageable pageable);

    @Query("""
            SELECT new com.web.appleshop.dto.response.admin.ProductFeatureDto(p.id, f.id, f.name, f.image)
                    FROM Product p
            JOIN p.features f
                    WHERE p.id IN :productIds
            """)
    Set<ProductFeatureDto> findFeaturesForProducts(@Param("productIds") Collection<Integer> productIds);

    Optional<Page<Product>> findAllByCategory_IdAndIsDeleted(Integer categoryId, Boolean isDeleted, Pageable pageable);

    Optional<Product> findProductByIdAndCategory_IdAndIsDeleted(Integer id, Integer categoryId, Boolean isDeleted);

    @Query("SELECT new map (p.category.id as categoryId, SUM(od.quantity) AS totalSold) FROM OrderDetail od JOIN od.product p JOIN od.order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status = 'DELIVERED' " +
            "GROUP BY p.category.id ORDER BY SUM(od.quantity) DESC")
    Page<Map<String, Object>> getSalesByCategory(Pageable pageable, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new map (s.color.id as colorId, SUM(od.quantity) AS totalSold) FROM OrderDetail od JOIN od.stock s JOIN od.order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status = 'DELIVERED' " +
            "GROUP BY s.color.id ORDER BY SUM(od.quantity) DESC")
    Page<Map<String, Object>> getSalesByColor(Pageable pageable, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<Product> findProductsByNameContainingIgnoreCase(String name);

    Optional<Product> findProductById(Integer id);

    void deleteProductById(Integer id);
}
