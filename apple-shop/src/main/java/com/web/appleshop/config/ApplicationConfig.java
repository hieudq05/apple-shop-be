package com.web.appleshop.config;

import com.web.appleshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configures application-level beans for security and user management.
 * <p>
 * This class provides central configuration for beans related to authentication,
 * user details services, and password encoding. It uses dependency injection
 * to wire the necessary components, such as the {@link UserRepository}.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    /**
     * Defines a bean for the {@link UserDetailsService}.
     * <p>
     * This service is responsible for loading user-specific data. It retrieves a user
     * by their email address from the {@link UserRepository}. If the user is not found,
     * it throws a {@link UsernameNotFoundException}.
     *
     * @return An implementation of {@link UserDetailsService} that loads users by email.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + username));
    }

    /**
     * Defines a bean for the {@link PasswordEncoder}.
     * <p>
     * This bean provides an instance of {@link BCryptPasswordEncoder}, which is used
     * for securely hashing and verifying passwords.
     *
     * @return A {@link BCryptPasswordEncoder} instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines a bean for the {@link AuthenticationManager}.
     * <p>
     * This manager is responsible for processing authentication requests. It is
     * retrieved from the {@link AuthenticationConfiguration}.
     *
     * @param config The authentication configuration provided by Spring Security.
     * @return The configured {@link AuthenticationManager}.
     * @throws Exception if an error occurs while retrieving the authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
