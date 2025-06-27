package com.web.appleshop.repository;

import com.web.appleshop.dto.response.admin.StockSummaryDto;
import com.web.appleshop.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface StockRepository extends JpaRepository<Stock, Integer>, JpaSpecificationExecutor<Stock> {
    @Query("""
                SELECT new com.web.appleshop.dto.response.admin.StockSummaryDto(
                    s.id, s.product.id, c.id, c.name, c.hexCode, s.quantity, s.price
                )
                FROM Stock s
                LEFT JOIN s.color c
                WHERE s.product.id IN :productIds
            """)
    Set<StockSummaryDto> findStockSummariesForProducts(@Param("productIds") List<Integer> productIds);
}