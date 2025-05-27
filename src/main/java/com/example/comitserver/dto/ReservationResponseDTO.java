package com.example.comitserver.dto;

import com.example.comitserver.entity.enumeration.Verification;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationResponseDTO {
    private Long id;
    private Long studyId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private UserResponseDTO reserver;
    private String title;
    private String description;
    private Verification isVerified;
} 