package com.web.appleshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
public class UserActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @ColumnDefault("getdate()")
    @Column(name = "LogTime")
    private Instant logTime;

    @Nationalized
    @Lob
    @Column(name = "ActionType", nullable = false)
    private String actionType;

    @Nationalized
    @Lob
    @Column(name = "TargetEntityType")
    private String targetEntityType;

    @Nationalized
    @Lob
    @Column(name = "Message")
    private String message;

    @Nationalized
    @Lob
    @Column(name = "OldValue")
    private String oldValue;

}