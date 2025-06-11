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

@RestController
@RequestMapping("otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;
    private final Environment environment;

    @PostMapping("generate")
    public ResponseEntity<ApiResponse<OtpResponse>> generate(@RequestBody OtpRequest request) {
        otpService.generateOtp(request.getEmail());
        OtpResponse response = new OtpResponse(request.getEmail(), Integer.parseInt(Objects.requireNonNull(environment.getProperty("otp.expired.in"))));
        return ResponseEntity.ok(
                ApiResponse.success(response, "Generate OTP successfully")
        );
    }

    @PostMapping("verify")
    public ResponseEntity<ApiResponse<OtpValidationResponse>> verify(@RequestBody OtpValidationRequest request) {
        boolean valid = otpService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(
                ApiResponse.success(new OtpValidationResponse(valid), "OTP verified successfully")
        );
    }
}
