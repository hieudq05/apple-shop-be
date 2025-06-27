package com.web.appleshop.repository;

import com.web.appleshop.dto.response.admin.ProductFeatureDto;
import com.web.appleshop.dto.response.admin.StockInstanceDto;
import com.web.appleshop.entity.InstanceProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

public interface InstancePropertyRepository extends JpaRepository<InstanceProperty, Integer>, JpaSpecificationExecutor<InstanceProperty> {
    @Query("""
            SELECT new com.web.appleshop.dto.response.admin.StockInstanceDto(s.id, ip.id, ip.name)
                    FROM Stock s
            JOIN s.instanceProperties ip
                    WHERE s.id IN :stockIds
            """)
    Set<StockInstanceDto> findInstancesForProducts(@Param("stockIds") Collection<Integer> stockIds);
}