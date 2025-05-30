package com.example.comitserver.controller;

import com.example.comitserver.dto.PostResponseDTO;
import com.example.comitserver.dto.ServerResponseDTO;
import com.example.comitserver.entity.PostEntity;
import com.example.comitserver.entity.enumeration.GroupType;
import com.example.comitserver.repository.PostRepository;
import com.example.comitserver.service.PostService;
import com.example.comitserver.utils.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class PostAdminController {

    private final PostService postService;
    private final ModelMapper modelMapper;
    private final PostRepository postRepository;

    @Autowired
    public PostAdminController(PostService postService, ModelMapper modelMapper, PostRepository postRepository) {
        this.postService = postService;
        this.modelMapper = modelMapper;
        this.postRepository = postRepository;
    }

    @GetMapping("/posts")
    public ResponseEntity<ServerResponseDTO> getAllPosts() {
        List<PostEntity> allPosts = postService.showAllPosts();
        List<PostResponseDTO> allPostsDTO = allPosts.stream()
                .map(entity -> modelMapper.map(entity, PostResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseUtil.createSuccessResponse(allPostsDTO, HttpStatus.OK);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<ServerResponseDTO> getPostById(@PathVariable Long id) {
        if (postRepository.findById(id).isEmpty())
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Post/CannotFindId", "post with that id not found");

        PostEntity post = postService.showPost(id);
        return ResponseUtil.createSuccessResponse(modelMapper.map(post, PostResponseDTO.class), HttpStatus.OK);
    }

    @GetMapping("/posts/{groupType}/{groupId}")
    public ResponseEntity<ServerResponseDTO> getPostByGroup(@PathVariable Long groupId, @PathVariable GroupType groupType) {
        List<PostEntity> posts = postService.showPostsByGroup(groupId, groupType);
        List<PostResponseDTO> postsDTO = posts.stream()
                .map(entity -> modelMapper.map(entity, PostResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseUtil.createSuccessResponse(postsDTO, HttpStatus.OK);
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
