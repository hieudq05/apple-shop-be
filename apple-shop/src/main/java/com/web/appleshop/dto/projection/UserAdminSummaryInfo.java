package com.web.appleshop.dto.projection;

import java.time.LocalDate;

/**
 * Projection for {@link com.web.appleshop.entity.User}
 */
public interface UserAdminSummaryInfo {
    Integer getId();

    String getEmail();

    String getPhone();

    String getFirstName();

    String getLastName();

    String getImage();

    Boolean getEnabled();

    LocalDate getBirth();
}