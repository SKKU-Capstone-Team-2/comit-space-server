package com.example.comitserver.dto;

import com.example.comitserver.entity.enumeration.Semester;
import lombok.Data;
import java.util.List;

@Data
public class EventRequestDTO {
//    private Long id;
    private String title;
    private String imageSrc;
    private String startTime;
    private String endTime;
    private String location;
    private List<String> tags;
    private String description;
    private Boolean isRecruiting;
    private Semester semester;
    private Integer year;
}
