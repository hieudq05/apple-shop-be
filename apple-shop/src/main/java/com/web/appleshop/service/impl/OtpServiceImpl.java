package com.web.appleshop.service.impl;

import com.web.appleshop.exception.ForbiddenException;
import com.web.appleshop.exception.IllegalStateException;
import com.web.appleshop.service.MailService;
import com.web.appleshop.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;

    @Value("${otp.length}")
    private int otpMaxLength;

    @Value("${otp.expired.in}")
    private int otpExpiredMinutes;

    /**
     * Tạo, lưu và trả về một mã OTP mới cho một định danh.
     * @param email Định danh của người dùng (email, số điện thoại).
     * @return Mã OTP đã tạo.
     */
    public String generateOtp(String email) {
        String rateLimitKey = "rate_limit:otp_request:" + email;

        if(redisTemplate.hasKey(rateLimitKey)) {
            throw new IllegalStateException("Too many OTP requests. Please try again later.");
        }

        String otp = generateRandomOtp(otpMaxLength);
        String key = "otp:" + email;

        redisTemplate.opsForValue().set(key, otp, otpExpiredMinutes, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(rateLimitKey, "blocked", 1, TimeUnit.MINUTES);

        mailService.sendMail(email, "OTP", "Your OTP is: " + otp);

        return otp;
    }

    /**
     * Xác thực mã OTP do người dùng cung cấp.
     * @param email Định danh của người dùng.
     * @param otp Mã OTP người dùng nhập.
     * @return true nếu hợp lệ, false nếu ngược lại.
     */
    public boolean verifyOtp(String email, String otp) {
        String key = "otp:" + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp != null && storedOtp.equals(otp)) {
            // OTP hợp lệ, xóa khỏi Redis để không thể sử dụng lại
            redisTemplate.delete(key);
            return true;
        }
        throw new ForbiddenException("Invalid OTP");
    }

    private String generateRandomOtp(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
