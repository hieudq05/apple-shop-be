package com.web.appleshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReplyReviewRequest {
    @NotBlank(message = "Nội dung phản hồi là bắt buộc")
    @Size(max = 1000, message = "Nội dung phản hồi có tối đa 1000 ký tự")
    private String replyContent;
}
