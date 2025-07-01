package com.web.appleshop.service;

import com.web.appleshop.dto.MailSender;
import com.web.appleshop.enums.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final MailProducer mailProducer;
    private final String frontendUrl = "http://localhost:5173/";
    private final String backendUrl = "http://localhost:8080/";

    public MailService(MailProducer mailProducer) {
        this.mailProducer = mailProducer;
    }

    public void sendMail(String to, String subject, String body) {
        MailSender mailer = MailSender.builder()
                .to(to)
                .subject(subject)
                .body(body)
                .build();
        mailProducer.sendEmailEvent(mailer);
    }

    public void sendUpdateOrderStatusMail(String to, String subject, OrderStatus newStatus, Integer orderId, OrderStatus oldStatus) {
        String body = "[UPDATE ORDER] Your order with id " + orderId + " has been " + newStatus.toString() + " from " + oldStatus.toString();
        sendMail(to, subject, body);
    }

    public void sendResetPasswordMail(String to, String token) {
        String resetUrl = frontendUrl + "reset-password?url=" + backendUrl + "?token=" + token;
        String subject = "Reset Password";
        String body = "[RESET PASSWORD] To reset your password, please click the link below: " + resetUrl;
        sendMail(to, subject, body);
    }
}
