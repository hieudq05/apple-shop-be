package com.web.appleshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.appleshop.dto.MailSender;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MailProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String TOPIC = "email-topic";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MailProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmailEvent(MailSender mailer) {
        try {
            String message = objectMapper.writeValueAsString(mailer);
            kafkaTemplate.send(TOPIC, message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
