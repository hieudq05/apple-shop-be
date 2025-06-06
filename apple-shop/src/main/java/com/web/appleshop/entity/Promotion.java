package com.web.appleshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "Name", nullable = false)
    private String name;

    @Nationalized
    @Column(name = "Code", nullable = false, length = 50)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PromotionType", nullable = false)
    private PromotionType promotionType;

    @Column(name = "\"Value\"", nullable = false, precision = 18, scale = 2)
    private BigDecimal value;

    @Column(name = "MaxDiscountAmount", precision = 18, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "MinOrderValue", precision = 18, scale = 2)
    private BigDecimal minOrderValue;

    @Column(name = "UsageLimit", nullable = false)
    private Integer usageLimit;

    @ColumnDefault("0")
    @Column(name = "UsageCount")
    private Integer usageCount;

    @ColumnDefault("1")
    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "StartDate", nullable = false)
    private Instant startDate;

    @Column(name = "EndDate", nullable = false)
    private Instant endDate;

    @Column(name = "applyOn", nullable = false)
    private Boolean applyOn = false;

    @ManyToMany(mappedBy = "promotions")
    private Set<Category> categories = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "promotions")
    private Set<Product> products = new LinkedHashSet<>();

}