package com.web.appleshop.dto.request;

import com.web.appleshop.enums.PromotionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePromotionRequest {
    @NotBlank(message = "Promotion name is required")
    @Size(max = 255, message = "Promotion name must not exceed 255 characters")
    private String name;

    @NotNull(message = "Promotion type is required")
    private PromotionType promotionType;

    @NotNull(message = "Promotion value is required")
    @DecimalMin(value = "0.01", message = "Promotion value must be greater than 0")
    private BigDecimal value;

    @DecimalMin(value = "0", message = "Max discount amount must be greater than or equal to 0")
    private BigDecimal maxDiscountAmount;

    @DecimalMin(value = "0", message = "Min order value must be greater than or equal to 0")
    private BigDecimal minOrderValue;

    @NotNull(message = "Usage limit is required")
    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @NotNull(message = "Apply on specification is required")
    private Boolean applyOn;

    private Set<Integer> categoryIds;

    private Set<Integer> stockIds;

    @AssertTrue(message = "End date must be after start date")
    public boolean isValidDateRange() {
        return endDate == null || startDate == null || endDate.isAfter(startDate);
    }

    @AssertTrue(message = "When applyOn is true, at least one category or product must be selected")
    public boolean isValidApplyOnSelection() {
        if (applyOn == null || !applyOn) {
            return true;
        }
        return (categoryIds != null && !categoryIds.isEmpty()) ||
                (stockIds != null && !stockIds.isEmpty());
    }
}
