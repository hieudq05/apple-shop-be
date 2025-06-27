package com.web.appleshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.appleshop.dto.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class MailConsumer {
    private final ObjectMapper objectMapper;
    private final JavaMailSenderImpl mailSender;

    @KafkaListener(topics = "email-topic", groupId = "mail-group")
    public void consumeEmailEvent(String message) {
        try {
            MailSender emailEvent = objectMapper.readValue(message, MailSender.class);
            sendEmail(emailEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(MailSender mailer) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailSender.getUsername());
        mailMessage.setTo(mailer.getTo());
        mailMessage.setSubject(mailer.getSubject());
        mailMessage.setText(mailer.getBody());
        mailSender.send(mailMessage);
    }
}
