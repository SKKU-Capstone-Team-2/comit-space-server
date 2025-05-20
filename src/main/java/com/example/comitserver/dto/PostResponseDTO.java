package com.example.comitserver.dto;

import com.example.comitserver.entity.enumeration.GroupType;
import lombok.Data;

@Data
public class PostResponseDTO {
    private Long id;
    private Long groupId;
    private GroupType groupType;
    private String title;
    private String content;
    private UserResponseDTO author;
    private String imageSrc;

}
