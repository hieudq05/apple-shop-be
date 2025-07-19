package com.web.appleshop.repository;

import com.web.appleshop.entity.OrderDetail;
import com.web.appleshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer>, JpaSpecificationExecutor<OrderDetail> {
    /**
     * Statistics
     */
    @Query("""
                select new map (od.product.id as productId, od.product.name as productName, od.product.category.id as categoryId, sum(od.quantity) as countSold)
                from OrderDetail od
                where od.order.createdAt between :fromDate and :toDate
                group by od.product.id, od.product.name, od.product.category.id
                order by sum(od.quantity) desc
            """)
    Page<Map<String, Object>> getTopSellingProducts(Pageable pageable, @Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}