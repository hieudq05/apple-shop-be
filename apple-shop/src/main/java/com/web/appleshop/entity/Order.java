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
public class Order {
    @Id
    private Long maDonHang;
    private LocalDate ngayMua;
    private String trangThaiDonHang;
    private Double tongTien;
    private String diaChiGiaoHang;
    private String trangThaiGiaoHang;
    private LocalDate ngayGiao;

    @ManyToOne
    @JoinColumn(name = "maNguoiDung")
    private NguoiDung nguoiDung;
}
