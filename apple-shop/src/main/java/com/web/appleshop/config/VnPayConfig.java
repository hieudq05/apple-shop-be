package com.web.appleshop.config;

import com.web.appleshop.exception.BadRequestException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Configuration properties for the VnPay payment gateway.
 * <p>
 * This class maps properties prefixed with {@code vnpay} from the application's
 * configuration file to its fields. It also provides utility methods for
 * generating HMAC-SHA512 signatures and retrieving the client's IP address,
 * which are required for VnPay integration.
 */
@Configuration
@ConfigurationProperties(prefix = "vnpay")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VnPayConfig {
    /** The URL for the VnPay payment gateway. */
    String url;
    /** The TmnCode (Terminal Code) provided by VnPay. */
    String tmnCode;
    /** The secret key used for generating HMAC signatures. */
    String secretKey;
    /** The version of the VnPay API. */
    String version;
    /** The command for the payment request (e.g., "pay"). */
    String command;
    /** The type of order. */
    String orderType;
    /** The URL to which VnPay redirects the user after payment. */
    String returnUrl;
    /** The URL for VnPay to send Instant Payment Notification (IPN) callbacks. */
    String ipnUrl;

    /**
     * Generates an HMAC-SHA512 hash for the given data using the specified key.
     * <p>
     * This method is used to create a secure signature for VnPay API requests, ensuring
     * data integrity and authenticity.
     *
     * @param key The secret key for hashing.
     * @param data The data to be hashed.
     * @return A hexadecimal string representation of the HMAC-SHA512 hash.
     * @throws BadRequestException if an error occurs during the hashing process.
     */
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            throw new BadRequestException("Lỗi khi tạo chữ ký.");
        }
    }

    /**
     * Retrieves the client's IP address from the HTTP request.
     * <p>
     * It first checks the {@code X-FORWARDED-FOR} header, which is common in environments
     * with reverse proxies. If that header is not present, it falls back to the remote
     * address from the request object.
     *
     * @param request The incoming HTTP servlet request.
     * @return The client's IP address as a string.
     */
    public static String getIpAddress(jakarta.servlet.http.HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }
}
