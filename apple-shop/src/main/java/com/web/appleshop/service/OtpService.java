package com.web.appleshop.service;

public interface OtpService {
    public String generateOtp(String email);

    public boolean verifyOtp(String email, String otp);
}
