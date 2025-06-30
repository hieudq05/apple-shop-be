package com.web.appleshop.dto.projection;

import java.time.LocalDateTime;

/**
 * Projection for {@link com.web.appleshop.entity.Blog}
 */
public interface BlogSummaryInfo {
    Integer getId();

    String getTitle();

    LocalDateTime getCreatedAt();

    String getStatus();

    OrderSummaryProjection.UserSummary getAuthor();
}