package com.web.appleshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProductId", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ColorId")
    private Color color;

    @ColumnDefault("0")
    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @OneToMany(mappedBy = "stock")
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    @OneToMany(mappedBy = "stock")
    private Set<OrderDetail> orderDetails = new LinkedHashSet<>();

    @OneToMany(mappedBy = "stock")
    private Set<ProductPhoto> productPhotos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "stock")
    private Set<SavedProduct> savedProducts = new LinkedHashSet<>();

    @ManyToMany
    private Set<InstanceProperty> instanceProperties = new LinkedHashSet<>();

}