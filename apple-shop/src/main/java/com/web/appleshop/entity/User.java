package com.web.appleshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "email", nullable = false)
    private String email;

    @Nationalized
    @Column(name = "phone", length = 20)
    private String phone;

    @Nationalized
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Nationalized
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Nationalized
    @Column(name = "last_name", length = 50)
    private String lastName;

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

    @Nationalized
    @Column(name = "image")
    private String image;

    @ColumnDefault("getdate()")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "author")
    private Set<Blog> blogs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<Feature> features = new LinkedHashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<InstanceProperty> instanceProperties = new LinkedHashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<Order> ordersCreated = new LinkedHashSet<>();

    @OneToMany(mappedBy = "approveBy")
    private Set<Order> ordersApproved = new LinkedHashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<Product> productsCreated = new LinkedHashSet<>();

    @OneToMany(mappedBy = "updatedBy")
    private Set<Product> productsUpdated = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Review> reviewsPosted = new LinkedHashSet<>();

    @OneToMany(mappedBy = "approvedBy")
    private Set<Review> reviewsApproved = new LinkedHashSet<>();

    @OneToMany(mappedBy = "repliedBy")
    private Set<Review> reviewsReplied = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<SavedProduct> savedProducts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<ShippingInfo> shippingInfos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserActivityLog> userActivityLogs = new LinkedHashSet<>();

}