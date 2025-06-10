package com.web.appleshop.service.impl;

import com.web.appleshop.entity.User;
import com.web.appleshop.exception.ForbiddenException;
import com.web.appleshop.exception.NotFoundException;
import com.web.appleshop.repository.UserRepository;
import com.web.appleshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    @Override
    public UserDetails findByLoginIdentifier(String loginIdentifier) {
        return userRepository.findByUsernameOrEmailOrPhone(loginIdentifier, loginIdentifier, loginIdentifier)
                .orElseThrow(() -> new NotFoundException("User not found with identifier: " + loginIdentifier));
    }

    @Override
    public User findUserByLoginIdentifier(String loginIdentifier) {
        User user = userRepository.getUserByUsernameOrEmailOrPhone(loginIdentifier, loginIdentifier, loginIdentifier)
                .orElseThrow(() -> new NotFoundException("User not found with identifier: " + loginIdentifier));
        return user;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}
