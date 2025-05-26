package com.example.comitserver.controller;

import com.example.comitserver.dto.PostResponseDTO;
import com.example.comitserver.dto.ServerResponseDTO;
import com.example.comitserver.entity.PostEntity;
import com.example.comitserver.repository.PostRepository;
import com.example.comitserver.service.PostService;
import com.example.comitserver.utils.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class PostAdminController {

    private final PostService postService;
    private final ModelMapper modelMapper;
    private final PostRepository postRepository;

    public PostAdminController(PostService postService, ModelMapper modelMapper, PostRepository postRepository) {
        this.postService = postService;
        this.modelMapper = modelMapper;
        this.postRepository = postRepository;
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ServerResponseDTO> deletePost(@PathVariable Long id) {
        if (postRepository.findById(id).isEmpty())
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Post/CannotFindId", "post with that id not found");

        PostEntity post = postService.showPost(id);
        PostResponseDTO postResponseDTO = modelMapper.map(post, PostResponseDTO.class);

        postService.deletePost(id);

        return ResponseUtil.createSuccessResponse(postResponseDTO, HttpStatus.OK);
    }
}
