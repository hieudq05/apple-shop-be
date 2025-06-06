package com.web.appleshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class StockInstance {
    @EmbeddedId
    private StockInstanceId id;

    @MapsId("instanceId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InstanceId", nullable = false)
    private InstanceProperty instance;

    @MapsId("stockId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "StockId", nullable = false)
    private Stock stock;

}