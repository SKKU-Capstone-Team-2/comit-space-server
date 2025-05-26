package com.example.comitserver.entity;

import com.example.comitserver.entity.enumeration.Verification;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reservation")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studyId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserver_id")
    private UserEntity reserver;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Verification isVerified;

    // getter, setter
} 