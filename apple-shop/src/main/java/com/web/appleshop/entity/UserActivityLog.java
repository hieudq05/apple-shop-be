package com.web.appleshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_activity_logs")
public class UserActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ColumnDefault("getdate()")
    @Column(name = "log_time")
    private LocalDateTime logTime;

    @Nationalized
    @Lob
    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Nationalized
    @Lob
    @Column(name = "target_entity_type")
    private String targetEntityType;

    @Nationalized
    @Lob
    @Column(name = "message")
    private String message;

    @Nationalized
    @Lob
    @Column(name = "old_value")
    private String oldValue;

    @Nationalized
    @Lob
    @Column(name = "new_value")
    private String newValue;

}