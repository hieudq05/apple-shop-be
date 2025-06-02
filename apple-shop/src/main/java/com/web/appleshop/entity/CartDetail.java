package com.web.appleshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDetail {
    @Id
    private Long cartDetailId;
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;
}
