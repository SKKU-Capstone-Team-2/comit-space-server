package com.example.comitserver.dto;

public class CommentResponseDTO {
    private Long id;
    private Long postId;
    private UserResponseDTO author; // 정보 노출 더 적게 하는 SimpleUserResponseDTO 고려 가능
    private String content;
}
