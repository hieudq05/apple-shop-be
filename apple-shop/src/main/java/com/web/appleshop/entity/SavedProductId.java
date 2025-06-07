package com.web.appleshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class SavedProductId implements Serializable {
    private static final long serialVersionUID = -849318116995174317L;
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "stock_id", nullable = false)
    private Integer stockId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SavedProductId entity = (SavedProductId) o;
        return Objects.equals(this.stockId, entity.stockId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockId, userId);
    }

}