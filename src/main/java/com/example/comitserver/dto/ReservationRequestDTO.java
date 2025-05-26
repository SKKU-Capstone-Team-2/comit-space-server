package com.example.comitserver.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationRequestDTO {
    private Long studyId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String title;
    private String description;
} 