package com.web.appleshop.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPham {
    @Id
    private Long maSanPham;
    private LocalDate ngayNhapKho;
    private LocalDate ngayBanRa;
    private Double gia;
    private String moTa;
    private String tenSanPham;
    private Integer soLuong;

    @ManyToOne
    @JoinColumn(name = "maDanhMuc")
    private DProduct danhMuc;
}
