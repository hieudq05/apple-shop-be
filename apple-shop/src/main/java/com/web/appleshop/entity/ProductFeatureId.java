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
public class ProductFeatureId implements Serializable {
    private static final long serialVersionUID = 178888710132989494L;
    @Column(name = "ProductId", nullable = false)
    private Integer productId;

    @Column(name = "FeatureId", nullable = false)
    private Integer featureId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductFeatureId entity = (ProductFeatureId) o;
        return Objects.equals(this.productId, entity.productId) &&
                Objects.equals(this.featureId, entity.featureId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, featureId);
    }

}