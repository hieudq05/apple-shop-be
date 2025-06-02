package com.web.appleshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDetail {
    @Id
    private Long maChiTietGioHang;
    private Integer soLuong;

    @ManyToOne
    @JoinColumn(name = "maSanPham")
    private SanPham sanPham;

    @OneToOne
    @JoinColumn(name = "maNguoiDung")
    private NguoiDung nguoiDung;
}
