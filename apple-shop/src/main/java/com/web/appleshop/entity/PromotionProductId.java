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
public class PromotionProductId implements Serializable {
    private static final long serialVersionUID = -6546947839921899657L;
    @Column(name = "PromotionId", nullable = false)
    private Integer promotionId;

    @Column(name = "ProductId", nullable = false)
    private Integer productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PromotionProductId entity = (PromotionProductId) o;
        return Objects.equals(this.productId, entity.productId) &&
                Objects.equals(this.promotionId, entity.promotionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, promotionId);
    }

}