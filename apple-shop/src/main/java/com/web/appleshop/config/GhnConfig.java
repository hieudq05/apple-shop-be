package com.web.appleshop.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ghn.api")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GhnConfig {
    String url;
    String token;
    String client_id;
    String shop_id;
}
