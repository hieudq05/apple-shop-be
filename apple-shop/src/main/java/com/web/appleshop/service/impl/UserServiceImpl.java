package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.RegisterRequest;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.UserRepository;
import com.web.appleshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails findByLoginIdentifier(String loginIdentifier) {
        return userRepository.findByUsernameOrEmailOrPhone(loginIdentifier, loginIdentifier, loginIdentifier)
                .orElseThrow(() -> new NotFoundException("User not found with identifier: " + loginIdentifier));
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}
