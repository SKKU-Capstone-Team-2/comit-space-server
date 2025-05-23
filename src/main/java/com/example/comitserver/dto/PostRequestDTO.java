package com.example.comitserver.dto;

import com.example.comitserver.entity.enumeration.GroupType;
import lombok.Data;

@Data
public class PostRequestDTO {
    private Long groupId;
    private GroupType groupType;
    private String title;
    private String content;
    private String imageSrc;
}
