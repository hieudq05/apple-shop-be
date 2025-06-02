package com.web.appleshop.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private Long orderId;
    private LocalDate orderDate;
    private String orderStatus;
    private Double totalAmount;
    private String deliveryAddress;
    private String shippingStatus;
    private LocalDate shippingDate;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
