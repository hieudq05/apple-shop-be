package com.web.appleshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBlogResponse {
    private Integer id;
    private String title;
    private String content;
    private String thumbnail;
    private String slug;
    private String metaDescription;
    private String tags;
    private Integer viewCount;
    private LocalDateTime publishedAt;
    private AuthorSummaryResponse author;

    // Excerpt của content (200 ký tự đầu)
    private String excerpt;

    @Data
    public static class AuthorSummaryResponse {
        private Integer id;
        private String firstName;
        private String lastName;
        private String image;
    }
}
