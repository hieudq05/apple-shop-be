package com.web.appleshop.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    private Long transactionId;
    private String status;
    private String paymentMethod;
    private LocalDate transactionDate;

    @OneToOne
    @JoinColumn(name = "orderId")
    private Order order;
}
