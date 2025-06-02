package com.web.appleshop.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToan {
    @Id
    private Long maGiaoDich;
    private String trangThai;
    private String phuongThucThanhToan;
    private LocalDate ngayGiaoDich;

    @OneToOne
    @JoinColumn(name = "maDonHang")
    private DonHang donHang;
}
