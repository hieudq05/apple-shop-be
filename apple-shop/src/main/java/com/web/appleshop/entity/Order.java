package com.web.appleshop.entity;

import com.web.appleshop.enums.OrderStatus;
import com.web.appleshop.enums.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"orders\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ColumnDefault("getdate()")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 55)
    private PaymentType paymentType;

    @ColumnDefault("getdate()")
    @Column(name = "approve_at")
    private LocalDateTime approveAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approve_by")
    private User approveBy;

    @Nationalized
    @Column(name = "first_name", length = 55)
    private String firstName;

    @Nationalized
    @Column(name = "last_name", length = 55)
    private String lastName;

    @Nationalized
    @Column(name = "email")
    private String email;

    @Nationalized
    @Column(name = "phone", length = 20)
    private String phone;

    @Nationalized
    @Column(name = "address", length = 500)
    private String address;

    @Nationalized
    @Column(name = "ward", length = 100)
    private String ward;

    @Nationalized
    @Column(name = "district", length = 100)
    private String district;

    @Nationalized
    @Column(name = "province", length = 100)
    private String province;

    @Nationalized
    @Column(name = "country", length = 100)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 55)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderDetail> orderDetails = new LinkedHashSet<>();

    @Size(max = 255)
    @Column(name = "shipping_tracking_code")
    private String shippingTrackingCode;

    @OneToMany(mappedBy = "order")
    private Set<Review> reviews = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_promotion_id")
    private Promotion productPromotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_promotion_id")
    private Promotion shippingPromotion;

    @ColumnDefault("0")
    @Column(name = "shipping_discount_amount", precision = 18, scale = 2)
    private BigDecimal shippingDiscountAmount;

    @ColumnDefault("0")
    @Column(name = "product_discount_amount", precision = 18, scale = 2)
    private BigDecimal productDiscountAmount;

    @Column(name = "subtotal", precision = 18, scale = 2)
    private BigDecimal subtotal;

    @ColumnDefault("0")
    @Column(name = "shipping_fee", precision = 18, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "final_total", precision = 18, scale = 2)
    private BigDecimal finalTotal;

}