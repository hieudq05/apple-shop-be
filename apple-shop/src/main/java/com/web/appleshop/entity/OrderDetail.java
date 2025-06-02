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
public class OrderDetail {
    @Id
    private Long maChiTietDonHang;
    private Integer soLuong;
    private Double donGia;

    @ManyToOne
    @JoinColumn(name = "maDonHang")
    private OrderDetail donHang;

    @ManyToOne
    @JoinColumn(name = "maSanPham")
    private SanPham sanPham;
}
