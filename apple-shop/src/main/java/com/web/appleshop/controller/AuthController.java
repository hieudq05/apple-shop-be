package com.web.appleshop.controller;

import com.web.appleshop.dto.request.LoginRequest;
import com.web.appleshop.dto.request.OtpValidationRequest;
import com.web.appleshop.dto.request.RegisterRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.AuthenticationResponse;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.repository.RefreshTokenRepository;
import com.web.appleshop.service.JwtService;
import com.web.appleshop.service.OtpService;
import com.web.appleshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLoginIdentifier(), request.getPassword())
        );

        User user = userService.findUserByLoginIdentifier(request.getLoginIdentifier());

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthenticationResponse response = new AuthenticationResponse(accessToken, refreshToken);

        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User userEntity = new User();
        BeanUtils.copyProperties(request, userEntity);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        userService.save(userEntity);
        otpService.generateOtp(request.getEmail());

        return ResponseEntity.ok(ApiResponse.success(null, "You have to verify your email to complete the registration."));
    }

    @PostMapping("register/verify")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> verify(@RequestBody OtpValidationRequest request) {
        otpService.verifyOtp(request.getEmail(), request.getOtp());
        User userEntity = userService.findUserByLoginIdentifier(request.getEmail());
        userEntity.setEnabled(true);
        userService.save(userEntity);

        String accessToken = jwtService.generateToken(userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);

        AuthenticationResponse response = new AuthenticationResponse(accessToken, refreshToken);

        return ResponseEntity.ok(ApiResponse.success(response, "Verified successfully, you can login now!"));
    }

    @PostMapping("refresh-token")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestBody String refreshToken) {
        jwtService.validateRefreshToken(refreshToken);
        UserDetails userDetails = userService.findByLoginIdentifier(jwtService.extractUsername(refreshToken));
        return ResponseEntity.ok(ApiResponse.success(jwtService.generateToken(userDetails), "Refreshed token successful"));
    }
}
