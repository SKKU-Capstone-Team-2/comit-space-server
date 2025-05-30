package com.example.comitserver.entity;

import com.example.comitserver.entity.enumeration.JoinState;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
public class CreatedEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JoinState state;
}
