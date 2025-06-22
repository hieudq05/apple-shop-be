package com.web.appleshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(min = 5, message = "Tiêu đề phải có ít nhất 5 ký tự")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    private String thumbnail;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status; // Có thể dùng Enum nếu muốn

    @NotNull(message = "ID người tạo không được để trống")
    private Integer authorId;
}
