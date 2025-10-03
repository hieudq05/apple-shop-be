package com.web.appleshop.controller;

import com.web.appleshop.dto.GoogleInfo;
import com.web.appleshop.dto.request.*;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.AuthenticationResponse;
import com.web.appleshop.dto.response.OtpResponse;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.repository.UserRepository;
import com.web.appleshop.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Handles authentication-related endpoints for the application.
 * <p>
 * This controller provides endpoints for user registration, login, token refreshing,
 * password management, and social authentication via Google. It coordinates with
 * various services to manage user identity, authentication tokens, and security.
 */
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuthService googleAuthService;
    private final Environment environment;
    private final RoleService roleService;
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;

    /**
     * Authenticates a user and returns access and refresh tokens.
     *
     * @param request The login request containing the user's email and password.
     * @return A {@link ResponseEntity} containing an {@link AuthenticationResponse} with tokens on success.
     */
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

    /**
     * Registers a new user and sends an OTP for email verification.
     *
     * @param request The registration request containing user details.
     * @return A {@link ResponseEntity} containing an {@link OtpResponse} with details for verification.
     */
    @PostMapping("register")
    public ResponseEntity<ApiResponse<OtpResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User userEntity = new User();
        BeanUtils.copyProperties(request, userEntity);
        userEntity.setRoles(roleService.findRoleByName("ROLE_USER"));
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        userRepository.save(userEntity);
        otpService.generateOtp(request.getEmail());

        OtpResponse response = new OtpResponse(request.getEmail(), Integer.parseInt(Objects.requireNonNull(environment.getProperty("otp.expired.in"))));

        return ResponseEntity.ok(ApiResponse.success(response, "You have to verify your email to complete the registration."));
    }

    /**
     * Verifies the user's email using an OTP and issues authentication tokens.
     *
     * @param request The OTP validation request containing the email and OTP.
     * @return A {@link ResponseEntity} containing an {@link AuthenticationResponse} with tokens on successful verification.
     */
    @PostMapping("register/verify")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> verify(@RequestBody OtpValidationRequest request) {
        otpService.verifyOtp(request.getEmail(), request.getOtp());
        User userEntity = userService.findUserByLoginIdentifier(request.getEmail());
        userEntity.setEnabled(true);
        userRepository.save(userEntity);

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

    /**
     * Initiates the password reset process by sending a reset link to the user's email.
     *
     * @param email The email address of the user who forgot their password.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping("forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestParam("email") String email) {
        if (email.isEmpty()) {
            throw new BadRequestException("Không có email nào được nhập. Vui lòng nhập email để đặt lại mật khẩu.");
        }
        passwordResetService.requestPasswordReset(email);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset email sent successfully"));
    }

    /**
     * Resets the user's password using a valid reset token.
     *
     * @param token The password reset token sent to the user's email.
     * @param password The new password.
     * @param confirmPassword The confirmation of the new password.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping("reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword
    ) {
        if (token.isEmpty()) {
            throw new BadRequestException("Không có token nào được nhập. Vui lòng nhập token để đặt lại mật khẩu. (Token: " + token + " )");
        }
        if (password.isEmpty()) {
            throw new BadRequestException("Không có mật khẩu nào được nhập. Vui lòng nhập mật khẩu để đặt lại.");
        }
        if (password.length() < 8) {
            throw new BadRequestException("Mật khẩu phải có 8 ký tự trở lên. Vui lòng nhập mật khẩu có 8 ký tự trở lên. (Password: " + password + " )");
        }
        if (password.length() > 32) {
            throw new BadRequestException("Mật khẩu không được quá 32 ký tự. Vui lòng nhập mật khẩu không quá 32 ký tự. (Password: " + password + " )");
        }
        if (!passwordResetService.validatePasswordResetToken(token)) {
            throw new BadRequestException("Request reset password was error, please try again or request a new one. (Token: " + token + " )");
        }
        passwordResetService.resetPassword(token, password);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successful"));
    }

    /**
     * Generates a new access token using a valid refresh token.
     *
     * @param refreshToken The refresh token.
     * @return A {@link ResponseEntity} containing the new access token.
     */
    @PostMapping("refresh-token")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        refreshToken = refreshToken.substring(7);
        UserDetails userDetails = userService.findByEmail(jwtService.extractUsername(refreshToken));
        jwtService.validateRefreshToken(refreshToken, userDetails);
        return ResponseEntity.ok(ApiResponse.success(jwtService.generateToken(userDetails), "Refreshed token successful"));
    }

    /**
     * Authenticates a user via their Google account.
     *
     * @param request The request containing the Google ID token.
     * @return A {@link ResponseEntity} containing an {@link AuthenticationResponse} with tokens on success.
     */
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

    /**
     * Logs out the currently authenticated user by invalidating their refresh token.
     *
     * @param refreshToken The refresh token to be invalidated.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping("logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody LogoutRequest refreshToken) {
        String token = refreshToken.getRefreshToken().substring(7);
        System.out.println(token);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (jwtService.extractUsername(token).equals(user.getEmail())) {
            jwtService.deleteRefreshToken(token);
        } else {
            throw new BadRequestException("Token này không phải của người dùng đang đăng nhập. Vui lòng xác minh rồi thử lại.");
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"));
    }
}
