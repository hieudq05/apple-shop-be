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
@Table(name = "\"Order\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CreatedBy", nullable = false)
    private User createdBy;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private Instant createdAt;

    @Nationalized
    @Column(name = "PaymentType", length = 50)
    private String paymentType;

    @ColumnDefault("getdate()")
    @Column(name = "ApproveAt")
    private Instant approveAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ApproveBy", nullable = false)
    private User approveBy;

    @Nationalized
    @Column(name = "FirstName", length = 55)
    private String firstName;

    @Nationalized
    @Column(name = "LastName", length = 55)
    private String lastName;

    @Nationalized
    @Column(name = "Email")
    private String email;

    @Nationalized
    @Column(name = "Phone", length = 20)
    private String phone;

    @Nationalized
    @Column(name = "Address", length = 500)
    private String address;

    @Nationalized
    @Column(name = "Ward", length = 100)
    private String ward;

    @Nationalized
    @Column(name = "District", length = 100)
    private String district;

    @Nationalized
    @Column(name = "Province", length = 100)
    private String province;

    @Nationalized
    @Column(name = "Country", length = 100)
    private String country;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Status", nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    private Set<OrderDetail> orderDetails = new LinkedHashSet<>();

}