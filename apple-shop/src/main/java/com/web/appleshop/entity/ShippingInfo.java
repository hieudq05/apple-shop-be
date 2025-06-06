package com.web.appleshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
public class ShippingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

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

    @ColumnDefault("0")
    @Column(name = "IsDefault")
    private Boolean isDefault;

}