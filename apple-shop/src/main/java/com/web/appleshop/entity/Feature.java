package com.web.appleshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Feature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Id", nullable = false)
    private User user;

    @Nationalized
    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Nationalized
    @Column(name = "Description", length = 500)
    private String description;

    @Nationalized
    @Column(name = "Image")
    private String image;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private Instant createdAt;

    @Column(name = "CreatedBy", nullable = false)
    private Integer createdBy;

    @ManyToMany
    @JoinTable(name = "ProductFeature",
            joinColumns = @JoinColumn(name = "FeatureId"),
            inverseJoinColumns = @JoinColumn(name = "ProductId"))
    private Set<Product> products = new LinkedHashSet<>();

}