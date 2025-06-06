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
public class PromotionCategoryId implements Serializable {
    private static final long serialVersionUID = 313150309512146119L;
    @Column(name = "PromotionId", nullable = false)
    private Integer promotionId;

    @Column(name = "CategoryId", nullable = false)
    private Integer categoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PromotionCategoryId entity = (PromotionCategoryId) o;
        return Objects.equals(this.promotionId, entity.promotionId) &&
                Objects.equals(this.categoryId, entity.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(promotionId, categoryId);
    }

}