package com.web.appleshop.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("webhooks")
public class WebhookController {
    @PostMapping("nhanhvn-callback")
    public String nhanhvnCallback() {
        // TODO: Implement webhook for Nhanh.vn
        return "OK";
    }
}
