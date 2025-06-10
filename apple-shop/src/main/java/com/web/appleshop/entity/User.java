package com.web.appleshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Nationalized;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"users\"")
@DynamicInsert
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "email", nullable = false)
    private String email;

    @Nationalized
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Nationalized
    @Column(name = "password", nullable = false)
    private String password;

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

    @ColumnDefault("0")
    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "username", nullable = false, length = 155)
    private String username;

    @NotNull
    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();

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

    @OneToMany(mappedBy = "user")
    private Set<RefreshToken> refreshTokens = new LinkedHashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}