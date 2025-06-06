package com.web.appleshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "ProductPhotos")
public class ProductPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "StockId", nullable = false)
    private Stock stock;

    @Nationalized
    @Column(name = "ImageUrl", nullable = false)
    private String imageUrl;

    @ColumnDefault("0")
    @Column(name = "IsDefault")
    private Boolean isDefault;

}