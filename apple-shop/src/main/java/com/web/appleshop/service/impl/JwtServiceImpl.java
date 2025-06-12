package com.web.appleshop.service.impl;

import com.web.appleshop.entity.RefreshToken;
import com.web.appleshop.service.JwtService;
import com.web.appleshop.service.RefreshTokenService;
import com.web.appleshop.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long expiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public JwtServiceImpl(RefreshTokenService refreshTokenService, UserService userService) {
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    /**
     * Trích xuất username (subject) từ token.
     *
     * @param token JWT token
     * @return String
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Trích xuất một claim cụ thể từ token.
     *
     * @param token          JWT token
     * @param claimsResolver - Một function để lấy claim mong muốn
     * @return T - Giá trị của claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Tạo access token từ UserDetails.
     *
     * @param userDetails Thông tin người dùng
     * @return Access token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Tạo access token với các claims thêm.
     *
     * @param extraClaims Các claims muốn thêm vào payload
     * @param userDetails Thông tin người dùng
     * @return Access token
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return "Bearer " + buildToken(extraClaims, userDetails, expiration);
    }

    /**
     * Tạo refresh token.
     *
     * @param userDetails Thông tin người dùng
     * @return Refresh token
     */
    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        String refreshToken = buildToken(new HashMap<>(), userDetails, refreshExpiration);

        Claims claims = extractAllClaims(refreshToken);

        refreshTokenService.save(createRefreshToken(userDetails, claims, refreshToken));

        return "Bearer " + refreshToken;
    }

    /**
     * Phương thức chính để xây dựng token.
     *
     * @param extraClaims Các claims muốn thêm vào payload
     * @param userDetails Thông tin người dùng
     * @param expiration  Thời gian hết hạn của token
     * @return Token
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Kiểm tra xem token có hợp lệ không.
     *
     * @param token       JWT token
     * @param userDetails Thông tin người dùng để so sánh
     * @return true nếu hợp lệ, false nếu không
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (
                username.equals(userDetails.getUsername())
                        && !isTokenExpired(token)
        );
    }

    /**
     * Kiểm tra xem token đã hết hạn chưa.
     *
     * @param token JWT token
     * @return true nếu đã hết hạn
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Trích xuất thời gian hết hạn từ token.
     *
     * @param token JWT token
     * @return Ngày hết hạn
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Trích xuất toàn bộ payload (claims) từ token.
     *
     * @param token JWT token
     * @return Claims object
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Lấy signing key từ secret key đã được mã hóa Base64.
     *
     * @return Key object
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private RefreshToken createRefreshToken(UserDetails userDetails, Claims claims, String refreshToken) {
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setIssuedAt(claims.getIssuedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        refreshTokenEntity.setExpiryDate(claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        refreshTokenEntity.setUser(userService.findUserByLoginIdentifier(userDetails.getUsername()));
        return refreshTokenEntity;
    }
}
