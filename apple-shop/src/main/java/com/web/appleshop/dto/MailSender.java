package com.web.appleshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailSender {
    String to;
    String subject;
    String body;
    String[] cc;
    String[] bcc;
    String[] attachments;
}
