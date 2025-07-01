package com.web.appleshop.service.impl;

import com.web.appleshop.entity.User;
import com.web.appleshop.repository.UserRepository;
import com.web.appleshop.service.MailService;
import com.web.appleshop.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.web.appleshop.exception.IllegalArgumentException;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void requestPasswordReset(String email) {
        User user = userRepository.getUserByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy người dùng nào có email: " + email)
        );
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token, user.getId().toString(), 15, TimeUnit.MINUTES);
        mailService.sendResetPasswordMail(user.getEmail(), token);
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        String userId = redisTemplate.opsForValue().get(token);
        return userId != null;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        String userId = redisTemplate.opsForValue().get(token);
        if (userId != null) {
            User user = userRepository.findById(Integer.parseInt(userId)).orElseThrow(
                    () -> new IllegalArgumentException("Không tìm thấy người dùng nào có id: " + userId)
            );
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            redisTemplate.delete(token);
        }
    }
}
