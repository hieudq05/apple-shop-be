package com.web.appleshop.dto.request;

import com.web.appleshop.enums.PromotionType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PromotionSearchRequest {
    private String keyword; // Tìm kiếm theo tên hoặc mã

    private Integer id; // Tìm kiếm theo id

    private String code; // Tìm kiếm chính xác theo mã

    private PromotionType promotionType; // Loại giảm giá

    private Boolean isActive; // Trạng thái active

    private Boolean applyOn; // Áp dụng cho sản phẩm/danh mục cụ thể

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateFrom; // Tìm kiếm theo ngày bắt đầu từ

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTo; // Tìm kiếm theo ngày bắt đầu đến

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateFrom; // Tìm kiếm theo ngày kết thúc từ

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTo; // Tìm kiếm theo ngày kết thúc đến

    private BigDecimal valueFrom; // Giá trị giảm từ

    private BigDecimal valueTo; // Giá trị giảm đến

    private BigDecimal minOrderValueFrom; // Giá trị đơn hàng tối thiểu từ

    private BigDecimal minOrderValueTo; // Giá trị đơn hàng tối thiểu đến

    private Integer usageLimitFrom; // Giới hạn sử dụng từ

    private Integer usageLimitTo; // Giới hạn sử dụng đến

    private Integer usageCountFrom; // Số lần đã sử dụng từ

    private Integer usageCountTo; // Số lần đã sử dụng đến

    private String sortBy; // Sắp xếp theo trường nào

    private String sortDirection; // ASC, DESC
}
