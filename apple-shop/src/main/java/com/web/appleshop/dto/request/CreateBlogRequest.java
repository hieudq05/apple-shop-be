package com.web.appleshop.dto.request;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.web.appleshop.entity.Blog}
 */
@Value
public class CreateBlogRequest implements Serializable {
    String title;
    String content;
    String thumbnail;
    Boolean isPublished;
}