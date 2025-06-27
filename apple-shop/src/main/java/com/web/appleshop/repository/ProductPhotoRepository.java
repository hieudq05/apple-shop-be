package com.web.appleshop.repository;

import com.web.appleshop.dto.response.admin.StockPhotoDtoLink;
import com.web.appleshop.dto.response.admin.StockSummaryDto;
import com.web.appleshop.entity.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Integer>, JpaSpecificationExecutor<ProductPhoto> {
    @Query("""
                SELECT new com.web.appleshop.dto.response.admin.StockPhotoDtoLink(
                    p.stock.id, p.id, p.imageUrl, p.alt
                )
                FROM ProductPhoto p
                WHERE p.stock.id IN :stockIds
            """)
    Set<StockPhotoDtoLink> findPhotosForStocks(@Param("stockIds") Set<Integer> stockIds);
}