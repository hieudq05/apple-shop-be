package com.web.appleshop.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    public UserDetails findByLoginIdentifier(String loginIdentifier);
}
