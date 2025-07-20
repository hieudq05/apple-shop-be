package com.web.appleshop.dto.projection;

import java.time.LocalDateTime;

/**
 * Projection for {@link com.web.appleshop.entity.Blog}
 */
public interface BlogForUserInfo {
    Integer getId();

    String getTitle();

    String getContent();

    String getThumbnail();

    LocalDateTime getPublishedAt();

    OrderSummaryProjection.UserSummary getAuthor();
}