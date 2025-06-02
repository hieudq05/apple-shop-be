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
    private Long maNguoiDung;
    private String email;
    private String tenNguoiDung;
    private String diaChi;
    private String matKhau;
    private String soDienThoai;
    private LocalDate ngaySinh;

    @ManyToOne
    @JoinColumn(name = "maPhanQuyen")
    private PhanQuyen phanQuyen;
}
