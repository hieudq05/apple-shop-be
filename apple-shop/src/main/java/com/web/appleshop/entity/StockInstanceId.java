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
public class StockInstanceId implements Serializable {
    private static final long serialVersionUID = 2434543737056942871L;
    @Column(name = "InstanceId", nullable = false)
    private Integer instanceId;

    @Column(name = "StockId", nullable = false)
    private Integer stockId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StockInstanceId entity = (StockInstanceId) o;
        return Objects.equals(this.instanceId, entity.instanceId) &&
                Objects.equals(this.stockId, entity.stockId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId, stockId);
    }

}