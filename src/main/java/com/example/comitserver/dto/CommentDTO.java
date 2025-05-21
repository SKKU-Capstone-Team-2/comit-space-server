package com.example.comitserver.dto;

public class CommentDTO {
    private Long id;
    private Long postId;
    private String content;
    private UserResponseDTO author; // 정보 노출 더 적게 하는 SimpleUserResponseDTO 고려 가능

}
