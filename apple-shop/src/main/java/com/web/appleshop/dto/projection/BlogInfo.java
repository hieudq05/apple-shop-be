package com.web.appleshop.dto.projection;

import java.time.LocalDateTime;

/**
 * Projection for {@link com.web.appleshop.entity.Blog}
 */
public interface BlogInfo {
    Integer getId();

    String getTitle();

    String getContent();

    String getThumbnail();

    LocalDateTime getPublishedAt();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    String getStatus();

    Boolean getIsPublished();

    OrderSummaryProjection.UserSummary getAuthor();
}