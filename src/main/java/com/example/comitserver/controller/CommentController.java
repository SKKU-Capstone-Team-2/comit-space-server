package com.example.comitserver.controller;

import com.example.comitserver.dto.CommentDTO;
import com.example.comitserver.dto.CustomUserDetails;
import com.example.comitserver.dto.ServerResponseDTO;
import com.example.comitserver.dto.UserResponseDTO;
import com.example.comitserver.entity.CommentEntity;
import com.example.comitserver.entity.UserEntity;
import com.example.comitserver.repository.CommentRepository;
import com.example.comitserver.repository.UserRepository;
import com.example.comitserver.service.CommentService;
import com.example.comitserver.utils.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("api")
public class CommentController {
    private final CommentService commentService;
    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentController(CommentService commentService, ModelMapper modelMapper, CommentRepository commentRepository, UserRepository userRepository) {
        this.commentService = commentService;
        this.modelMapper = modelMapper;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("posts/{postId}/comments")
    public ResponseEntity<ServerResponseDTO> postComment(@RequestBody CommentDTO commentDTO,
                                                         @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                         @PathVariable Long postId) {
        commentDTO.setPostId(postId);
        CommentEntity newComment = commentService.createComment(commentDTO, customUserDetails);
        return ResponseUtil.createSuccessResponse(modelMapper.map(newComment, CommentDTO.class), HttpStatus.CREATED);
//        // modelMapper는 userId → author 로 자동 매핑해줄 수 없기 때문에, author 수동 설정
//        CommentDTO responseDTO = modelMapper.map(newComment, CommentDTO.class); // CommentEntity에 userId, CommentDTO에 author
//        UserEntity author = userRepository.findById(newComment.getUserId())
//                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + newComment.getUserId()));
//
//        UserResponseDTO authorDTO = modelMapper.map(author, UserResponseDTO.class);
//        responseDTO.setAuthor(authorDTO);
//
//        return ResponseUtil.createSuccessResponse(responseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ServerResponseDTO> deleteComment(@PathVariable Long id,
                                                           @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (commentRepository.findById(id).isEmpty())
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Comment/CannotFindId", "comment with that id not found");

        if (commentService.identification(id, customUserDetails)) {
            commentService.deleteComment(id);
            return ResponseUtil.createSuccessResponse("Comment deleted successfully", HttpStatus.OK);
        } else {
            return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Comment/PermissionDenied", "the user does not have permission to delete this comment");
        }
    }
}
