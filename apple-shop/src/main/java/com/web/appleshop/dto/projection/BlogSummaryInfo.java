package com.web.appleshop.dto.projection;

import java.time.LocalDateTime;

/**
 * Projection for {@link com.web.appleshop.entity.Blog}
 */
public interface BlogSummaryInfo {
    Integer getId();

    String getTitle();

    String getThumbnail();

    LocalDateTime getCreatedAt();

    String getStatus();

    Boolean getIsPublished();

    OrderSummaryProjection.UserSummary getAuthor();
}