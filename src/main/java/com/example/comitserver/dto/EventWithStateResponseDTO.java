package com.example.comitserver.dto;

import com.example.comitserver.entity.enumeration.Semester;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EventWithStateResponseDTO {
    private EventResponseDTO event;
    private String state;
}
