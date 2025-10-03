package com.web.appleshop.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles incoming webhooks from external services.
 * <p>
 * This controller provides endpoints to receive notifications and data from
 * third-party systems, such as payment gateways or shipping providers.
 */
@RestController
@RequestMapping("webhooks")
public class WebhookController {

    /**
     * A callback endpoint for Nhanh.vn service.
     * <p>
     * This endpoint is intended to receive notifications from the Nhanh.vn
     * shipping service. It currently returns a simple "OK" to acknowledge
     * receipt of the webhook.
     *
     * @return A string "OK" to acknowledge the request.
     */
    @PostMapping("nhanhvn-callback")
    public String nhanhvnCallback() {
        return "OK";
    }
}
