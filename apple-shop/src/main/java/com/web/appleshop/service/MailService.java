package com.web.appleshop.service;

import com.web.appleshop.dto.MailSender;
import com.web.appleshop.enums.OrderStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSenderImpl mailSender;
    private final MailProducer mailProducer;

    public MailService(JavaMailSenderImpl mailSender, MailProducer mailProducer) {
        this.mailSender = mailSender;
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
}
