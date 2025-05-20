package com.example.comitserver.dto;

import lombok.Data;

@Data
public class PostResponseDTO {
    private Long id;
    private String title;
    private String content;
    private UserResponseDTO author;
    private String imageSrc;

}
