package com.web.appleshop.service;

import com.web.appleshop.dto.request.OtpValidationRequest;
import com.web.appleshop.dto.request.RegisterRequest;
import com.web.appleshop.dto.response.AuthenticationResponse;
import com.web.appleshop.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    public UserDetails findByLoginIdentifier(String loginIdentifier);

    public User findUserByLoginIdentifier(String loginIdentifier);

    public void save(User user);

}
