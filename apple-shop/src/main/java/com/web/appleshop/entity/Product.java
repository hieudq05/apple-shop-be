package com.web.appleshop.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private Long productId;
    private LocalDate importDate;
    private LocalDate releaseDate;
    private Double price;
    private String description;
    private String productName;
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;
}
