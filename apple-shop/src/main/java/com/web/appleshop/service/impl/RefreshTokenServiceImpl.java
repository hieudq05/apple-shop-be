package com.web.appleshop.service.impl;

import com.web.appleshop.entity.RefreshToken;
import com.web.appleshop.repository.RefreshTokenRepository;
import com.web.appleshop.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }


}
