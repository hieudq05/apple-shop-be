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

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

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
            p.id, p.name, p.description, p.createdAt, CONCAT(p.createdBy.firstName, ' ', p.createdBy.lastName), p.category.id, p.category.name)
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
}
