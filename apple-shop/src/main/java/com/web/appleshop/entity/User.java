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
@Table(name = "\"User\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "Email", nullable = false)
    private String email;

    @Nationalized
    @Column(name = "Phone", length = 20)
    private String phone;

    @Nationalized
    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @Nationalized
    @Column(name = "FirstName", length = 50)
    private String firstName;

    @Nationalized
    @Column(name = "LastName", length = 50)
    private String lastName;

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

    @Nationalized
    @Column(name = "Image")
    private String image;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private Instant createdAt;

    @Column(name = "UpdatedAt")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoleId")
    private Role role;

    @ColumnDefault("1")
    @Column(name = "IsActive")
    private Boolean isActive;

    @OneToMany(mappedBy = "author")
    private Set<Blog> blogs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    @OneToOne(mappedBy = "id")
    private Feature feature;

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
    private Set<Review> reviews = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<SavedProduct> savedProducts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<ShippingInfo> shippingInfos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserActivityLog> userActivityLogs = new LinkedHashSet<>();

}