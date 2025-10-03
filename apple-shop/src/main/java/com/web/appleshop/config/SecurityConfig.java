package com.web.appleshop.config;

import com.web.appleshop.security.CustomAuthExceptionHandler;
import com.web.appleshop.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configures the security settings for the application.
 * <p>
 * This class enables web security and method-level security, and defines the
 * security filter chain, CORS configuration, and other security-related beans.
 * It integrates a custom JWT authentication filter and exception handlers.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthExceptionHandler customAuthExceptionHandler;

    /**
     * Defines the security filter chain that applies to all HTTP requests.
     * <p>
     * This configuration specifies:
     * <ul>
     *     <li>CSRF protection is disabled.</li>
     *     <li>CORS is enabled with a custom configuration source.</li>
     *     <li>Public access is granted to specific endpoints (e.g., {@code /auth/**}, {@code /products/**}).</li>
     *     <li>All other requests require authentication.</li>
     *     <li>Stateless session management is enforced, suitable for REST APIs.</li>
     *     <li>Custom exception handling for authentication and access denied errors.</li>
     *     <li>The {@link JwtAuthenticationFilter} is added before the standard password authentication filter.</li>
     * </ul>
     *
     * @param http The {@link HttpSecurity} object to configure.
     * @return The configured {@link SecurityFilterChain}.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
                csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/products/**").permitAll()
                        .requestMatchers("/colors/**").permitAll()
                        .requestMatchers("/otp/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/blogs/**").permitAll()
                        .requestMatchers("/categories/**").permitAll()
                        .requestMatchers("/payments/vnpay/call-back").permitAll()
                        .requestMatchers("/payments/paypal/success").permitAll()
                        .requestMatchers("/payments/paypal/cancel").permitAll()
                        .requestMatchers("/webhooks/**").permitAll()
                        .requestMatchers("/reviews/product/**").permitAll()
                        .requestMatchers("/ws-chat/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthExceptionHandler)
                        .accessDeniedHandler(customAuthExceptionHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Defines the CORS (Cross-Origin Resource Sharing) configuration.
     * <p>
     * This bean configures which origins, methods, and headers are allowed for
     * cross-origin requests. It is essential for allowing frontend applications
     * running on different domains (e.g., {@code http://localhost:5173}) to
     * communicate with the API.
     *
     * @return A {@link CorsConfigurationSource} with the defined CORS rules.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173/"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
