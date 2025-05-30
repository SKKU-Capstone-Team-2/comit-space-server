package com.example.comitserver.controller;

import com.example.comitserver.dto.CommentDTO;
import com.example.comitserver.dto.ServerResponseDTO;
import com.example.comitserver.entity.CommentEntity;
import com.example.comitserver.repository.CommentRepository;
import com.example.comitserver.service.CommentService;
import com.example.comitserver.utils.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class CommentAdminController {

    private final CommentService commentService;
    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentAdminController(CommentService commentService, ModelMapper modelMapper, CommentRepository commentRepository) {
        this.commentService = commentService;
        this.modelMapper = modelMapper;
        this.commentRepository = commentRepository;
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ServerResponseDTO> deleteComment(@PathVariable Long id) {
        if (commentRepository.findById(id).isEmpty())
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Comment/CannotFindId", "comment with that id not found");

        CommentEntity comment = commentService.showComment(id);
        CommentDTO commentResponseDTO = modelMapper.map(comment, CommentDTO.class);

        commentService.deleteComment(id);

        return ResponseUtil.createSuccessResponse(commentResponseDTO, HttpStatus.OK);
    }
}
