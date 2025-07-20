package com.web.appleshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserCreateReviewRequest {
    @NotNull(message = "Đánh giá sao là bắt buộc")
    @Min(value = 1, message = "Đánh giá phải từ 1 đến 5")
    @Max(value = 5, message = "Đánh giá phải từ 1 đến 5")
    private Integer rating;

    @NotNull(message = "Mã stock là bắt buộc")
    private Integer stockId;

    @NotNull(message = "Mã đơn hàng là bắt buộc")
    private Integer orderId;

    @NotBlank(message = "Nội dung là bắt buộc")
    @Size(max = 1000, message = "Nội dung độ dài trong khoảng 1 đến 1000 ký tự")
    private String content;
}
