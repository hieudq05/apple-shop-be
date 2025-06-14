package com.web.appleshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "stocks")
@DynamicInsert
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @ColumnDefault("0")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductPhoto> productPhotos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SavedProduct> savedProducts = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "stocks")
    private Set<InstanceProperty> instanceProperties = new LinkedHashSet<>();

    public void addInstanceProperty(InstanceProperty instanceProperty) {
        this.instanceProperties.add(instanceProperty);
        instanceProperty.getStocks().add(this);
    }

    public void removeInstanceProperty(InstanceProperty instanceProperty) {
        this.instanceProperties.remove(instanceProperty);
        instanceProperty.getStocks().remove(this);
    }

}