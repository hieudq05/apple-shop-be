package com.web.appleshop.dto.response.admin;

import com.web.appleshop.dto.response.UserReviewDto;
import com.web.appleshop.enums.PromotionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminPromotionDto {
    private Integer id;
    private String name;
    private String code;
    private PromotionType promotionType;
    private BigDecimal value;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderValue;
    private Integer usageLimit;
    private Integer usageCount;
    private Boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean applyOn;
    private List<CategorySummary> categories;
    private List<StockSummary> stocks;
    private LocalDateTime createdAt;
    private UserReviewDto.UserDto createdBy;

    @Data
    public static class CategorySummary {
        private Integer id;
        private String name;
    }

    @Data
    public static class StockSummary {
        private Integer id;
        private String colorName;
        private Set<InstanceSummary> instances;

        @Data
        public static class InstanceSummary {
            private Integer id;
            private String name;
        }
    }
}
