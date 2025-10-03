package com.web.appleshop.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the Giao Hang Nhanh (GHN) API.
 * <p>
 * This class maps properties prefixed with {@code ghn.api} from the application's
 * configuration file (e.g., {@code application.properties} or {@code application.yml})
 * to the fields of this class. It is used to configure credentials and endpoints
 * for interacting with the GHN shipping service.
 */
@Configuration
@ConfigurationProperties(prefix = "ghn.api")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GhnConfig {
    /**
     * The base URL of the GHN API.
     */
    String url;
    /**
     * The authentication token for accessing the GHN API.
     */
    String token;
    /**
     * The client ID provided by GHN for API authentication.
     */
    String client_id;
    /**
     * The shop ID registered with GHN.
     */
    String shop_id;
}
