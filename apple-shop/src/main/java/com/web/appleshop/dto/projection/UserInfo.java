package com.web.appleshop.dto.projection;

import java.time.LocalDate;

/**
 * Projection for {@link com.web.appleshop.entity.User}
 */
public interface UserInfo {
    Integer getId();

    String getEmail();

    String getPhone();

    String getFirstName();

    String getLastName();

    LocalDate getBirth();

    String getImage();
}