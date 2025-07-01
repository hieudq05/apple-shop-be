package com.web.appleshop.dto.projection;

/**
 * Projection for {@link com.web.appleshop.entity.User}
 */
public interface UserInfo {
    Integer getId();

    String getEmail();

    String getPhone();

    String getFirstName();

    String getLastName();

    String getImage();
}