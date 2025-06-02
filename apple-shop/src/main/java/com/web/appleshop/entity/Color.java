package com.web.appleshop.entity;

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
public class Color {
    @Id
    private Long maMau;
    private String tenMau;
    private String maMauCode;

    @ManyToOne
    @JoinColumn(name = "maSanPham")
    private SanPham sanPham;
}
