package com.example.comitserver.dto;

import com.example.comitserver.entity.enumeration.Campus;
import com.example.comitserver.entity.enumeration.Day;
import com.example.comitserver.entity.enumeration.Level;
import com.example.comitserver.entity.enumeration.Semester;
import lombok.Data;

import java.util.List;

@Data
public class StudyWithStateResponseDTO {
    private StudyResponseDTO study;
    private String state;
}
