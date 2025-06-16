package com.web.appleshop.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.web.appleshop.dto.GoogleInfo;
import com.web.appleshop.dto.response.AuthenticationResponse;
import com.web.appleshop.entity.User;
import com.web.appleshop.exception.BadRequestException;
import com.web.appleshop.repository.RoleRepository;
import com.web.appleshop.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleAuthService {

    private static final Logger log = LoggerFactory.getLogger(GoogleAuthService.class);
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    @Value("${google.client-id}")
    private String clientId;

    public GoogleAuthService(UserRepository userRepository, JwtService jwtService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
    }

    public GoogleIdToken verifyGoogleToken(String token) {
        GoogleIdToken googleIdToken;
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singleton(clientId))
                .build();

        try {
            googleIdToken = verifier.verify(token);
        } catch (GeneralSecurityException e) {
            throw new BadRequestException("Lỗi khi xác thực token.");
        } catch (IOException e) {
            throw new BadRequestException("Google token không hợp lệ.");
        }

        if (googleIdToken == null) {
            throw new BadRequestException("Google token không hợp lệ.");
        }

        return googleIdToken;
    }

    public GoogleInfo getInfo(GoogleIdToken googleIdToken) {
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        return GoogleInfo.builder()
                .email(payload.getEmail())
                .name((String) payload.get("name"))
                .imageUrl((String) payload.get("picture"))
                .build();
    }

    @Transactional
    public User registerByGoogle(GoogleInfo googleInfo) {
        User userRegister = userRepository.findUserByEmail(googleInfo.getEmail()).orElse(null);
        if (userRegister == null) {
            userRegister = User.builder()
                    .email(googleInfo.getEmail())
                    .firstName(googleInfo.getName())
                    .enabled(true)
                    .roles(
                            roleRepository.findRoleByName("ROLE_USER")
                    )
                    .build();
            userRepository.save(userRegister);
        }
        return userRegister;
    }
}
