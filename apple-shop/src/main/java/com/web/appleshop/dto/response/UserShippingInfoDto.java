package com.web.appleshop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * DTO for {@link com.web.appleshop.entity.ShippingInfo}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserShippingInfoDto implements Serializable {
    Integer id;
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
    String ward;
    String district;
    String province;
    Boolean isDefault;
}