package com.web.appleshop.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(value = JsonInclude.Include.NON_NULL)
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
    private LocalDateTime createdAt;
    private UserReviewDto.UserDto createdBy;
}
