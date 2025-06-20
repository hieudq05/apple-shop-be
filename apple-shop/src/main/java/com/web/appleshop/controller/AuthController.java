package com.web.appleshop.controller;

import com.web.appleshop.dto.GoogleInfo;
import com.web.appleshop.dto.request.GgTokenRequest;
import com.web.appleshop.dto.request.LoginRequest;
import com.web.appleshop.dto.request.OtpValidationRequest;
import com.web.appleshop.dto.request.RegisterRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.AuthenticationResponse;
import com.web.appleshop.dto.response.OtpResponse;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.repository.RefreshTokenRepository;
import com.web.appleshop.service.GoogleAuthService;
import com.web.appleshop.service.JwtService;
import com.web.appleshop.service.OtpService;
import com.web.appleshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GoogleAuthService googleAuthService;
    private final Environment environment;

    @PostMapping("login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userService.findUserByLoginIdentifier(request.getEmail());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", user.getAuthorities());
        extraClaims.put("imageUrl", user.getImage());
        extraClaims.put("firstName", user.getFirstName());
        extraClaims.put("lastName", user.getLastName());

        String accessToken = jwtService.generateToken(extraClaims, user);
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthenticationResponse response = new AuthenticationResponse(accessToken, refreshToken);

        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("register")
    public ResponseEntity<ApiResponse<OtpResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User userEntity = new User();
        BeanUtils.copyProperties(request, userEntity);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        userService.save(userEntity);
        otpService.generateOtp(request.getEmail());

        OtpResponse response = new OtpResponse(request.getEmail(), Integer.parseInt(Objects.requireNonNull(environment.getProperty("otp.expired.in"))));

        return ResponseEntity.ok(ApiResponse.success(response, "You have to verify your email to complete the registration."));
    }

    @PostMapping("register/verify")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> verify(@RequestBody OtpValidationRequest request) {
        otpService.verifyOtp(request.getEmail(), request.getOtp());
        User userEntity = userService.findUserByLoginIdentifier(request.getEmail());
        userEntity.setEnabled(true);
        userService.save(userEntity);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", userEntity.getAuthorities());
        extraClaims.put("imageUrl", userEntity.getImage());
        extraClaims.put("firstName", userEntity.getFirstName());
        extraClaims.put("lastName", userEntity.getLastName());

        String accessToken = jwtService.generateToken(extraClaims, userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);

        AuthenticationResponse response = new AuthenticationResponse(accessToken, refreshToken);

        return ResponseEntity.ok(ApiResponse.success(response, "Verified successfully, you can login now!"));
    }

    @PostMapping("refresh-token")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        refreshToken = refreshToken.substring(7);
        UserDetails userDetails = userService.findByEmail(jwtService.extractUsername(refreshToken));
        jwtService.validateRefreshToken(refreshToken, userDetails);
        return ResponseEntity.ok(ApiResponse.success(jwtService.generateToken(userDetails), "Refreshed token successful"));
    }

    @PostMapping("google")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> googleAuth(@RequestBody GgTokenRequest request) {
        GoogleInfo googleInfo = googleAuthService.getInfo(googleAuthService.verifyGoogleToken(request.getToken()));

        User user = googleAuthService.registerByGoogle(googleInfo);
        AuthenticationResponse response = new AuthenticationResponse();

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", user.getAuthorities());
        extraClaims.put("imageUrl", user.getImage());
        extraClaims.put("firstName", user.getFirstName());
        extraClaims.put("lastName", user.getLastName());

        response.setAccessToken(jwtService.generateToken(extraClaims, user));
        response.setRefreshToken(jwtService.generateRefreshToken(user));

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "Login by Google successful"
        ));
    }
}
