package com.web.appleshop.dto.response;

import com.web.appleshop.enums.PromotionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.web.appleshop.entity.Promotion}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPromotionDto implements Serializable {
    String name;
    String code;
    PromotionType promotionType;
    BigDecimal value;
    BigDecimal maxDiscountAmount;
    BigDecimal minOrderValue;
    LocalDateTime startDate;
    LocalDateTime endDate;
}