package com.web.appleshop.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private Long userId;
    private String email;
    private String fullName;
    private String address;
    private String password;
    private String phoneNumber;
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;
}
