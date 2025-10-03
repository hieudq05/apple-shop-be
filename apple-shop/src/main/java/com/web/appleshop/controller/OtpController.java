package com.web.appleshop.controller;

import com.web.appleshop.dto.request.OtpRequest;
import com.web.appleshop.dto.request.OtpValidationRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.OtpResponse;
import com.web.appleshop.dto.response.OtpValidationResponse;
import com.web.appleshop.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * Handles One-Time Password (OTP) generation and verification.
 * <p>
 * This controller provides endpoints to generate an OTP for a given email address
 * and to verify an OTP, which is a crucial part of the user registration and
 * authentication process.
 */
@RestController
@RequestMapping("otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;
    private final Environment environment;

    /**
     * Generates and sends an OTP to the specified email address.
     *
     * @param request The request containing the email to which the OTP should be sent.
     * @return A {@link ResponseEntity} with an {@link OtpResponse} containing the email
     *         and the OTP's expiration time.
     */
    @PostMapping("generate")
    public ResponseEntity<ApiResponse<OtpResponse>> generate(@RequestBody OtpRequest request) {
        otpService.generateOtp(request.getEmail());
        OtpResponse response = new OtpResponse(request.getEmail(), Integer.parseInt(Objects.requireNonNull(environment.getProperty("otp.expired.in"))));
        return ResponseEntity.ok(
                ApiResponse.success(response, "OTP generated successfully")
        );
    }

    /**
     * Verifies the provided OTP for the given email address.
     *
     * @param request The request containing the email and the OTP to be verified.
     * @return A {@link ResponseEntity} with an {@link OtpValidationResponse} indicating
     *         whether the OTP is valid.
     */
    @PostMapping("verify")
    public ResponseEntity<ApiResponse<OtpValidationResponse>> verify(@RequestBody OtpValidationRequest request) {
        boolean valid = otpService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(
                ApiResponse.success(new OtpValidationResponse(valid), "OTP verified successfully")
        );
    }
}
