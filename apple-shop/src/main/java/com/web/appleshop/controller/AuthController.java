package com.web.appleshop.controller;

import com.web.appleshop.dto.request.LoginRequest;
import com.web.appleshop.dto.request.RegisterRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.AuthenticationResponse;
import com.web.appleshop.entity.User;
import com.web.appleshop.service.JwtService;
import com.web.appleshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLoginIdentifier(), request.getPassword())
        );

        UserDetails user = userService.findByLoginIdentifier(request.getLoginIdentifier());

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthenticationResponse response = new AuthenticationResponse(accessToken, refreshToken);

        return ResponseEntity.ok(ApiResponse.success(response, "Login successfully"));
    }

    @PostMapping("register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User userEntity = new User();
        BeanUtils.copyProperties(request, userEntity);

        userService.save(userEntity);

        String accessToken = jwtService.generateToken(userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);

        AuthenticationResponse response = new AuthenticationResponse(accessToken, refreshToken);

        return ResponseEntity.ok(ApiResponse.success(response, "Register successfully"));
    }
}
