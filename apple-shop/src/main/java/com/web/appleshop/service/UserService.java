package com.web.appleshop.service;

import com.web.appleshop.dto.GoogleInfo;
import com.web.appleshop.dto.request.OtpValidationRequest;
import com.web.appleshop.dto.request.RegisterRequest;
import com.web.appleshop.dto.response.AuthenticationResponse;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserDetails findByEmail(String email);

    User findUserByLoginIdentifier(String email);

    void save(User user);

    ProductAdminResponse.ProductOwnerAdminResponse convertUserToProductOwnerAdminResponse(User user);
}
