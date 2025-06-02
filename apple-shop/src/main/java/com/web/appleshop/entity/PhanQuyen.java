package com.web.appleshop.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "phanquyen")
public class PhanQuyen {
    @Id
    private Long maPhanQuyen;
    private String tenQuyen;
}
