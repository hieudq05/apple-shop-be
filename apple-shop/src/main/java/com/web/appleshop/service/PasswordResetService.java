package com.web.appleshop.service;

public interface PasswordResetService {
    void requestPasswordReset(String email);

    boolean validatePasswordResetToken(String token);

    void resetPassword(String token, String newPassword);
}
