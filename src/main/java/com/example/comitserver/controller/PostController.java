package com.example.comitserver.controller;

import com.example.comitserver.dto.*;
import com.example.comitserver.entity.PostEntity;
import com.example.comitserver.entity.enumeration.GroupType;
import com.example.comitserver.repository.PostRepository;
import com.example.comitserver.service.PostService;
import com.example.comitserver.utils.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class PostController {
    private final PostService postService;
    private final ModelMapper modelMapper;
    private final PostRepository postRepository;

    @Autowired
    public PostController(PostService postService, ModelMapper modelMapper, PostRepository postRepository) {
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
        if(postRepository.findById(id).isEmpty())
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Post/CannotFindId", "post with that id not found");

        PostEntity post = postService.showPost(id);
        return ResponseUtil.createSuccessResponse(modelMapper.map(post, PostResponseDTO.class), HttpStatus.OK);
    }

    // group에 속한 사람만 생성 가능하도록 추가 예정
    @PostMapping("/posts/{groupType}/{groupId}")
    public ResponseEntity<ServerResponseDTO> postPost(@RequestBody PostRequestDTO postRequestDTO,
                                                      @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @PathVariable GroupType groupType, @PathVariable Long groupId) {
        postRequestDTO.setGroupType(groupType);
        postRequestDTO.setGroupId(groupId);

        PostEntity newPost = postService.createPost(postRequestDTO, customUserDetails);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath() // 현재 context path(/api) 기준으로
                .path("/posts/{id}") // /posts/{id} 추가
                .buildAndExpand(newPost.getId())
                .toUri();


        return ResponseUtil.createSuccessResponse(modelMapper.map(newPost, PostResponseDTO.class), HttpStatus.CREATED, location);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<ServerResponseDTO> putPost(@PathVariable Long id,
                                                     @RequestBody PostRequestDTO postRequestDTO,
                                                     @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (postRepository.findById(id).isEmpty())
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Post/CannotFindId", "post with that id not found");

        if (postService.identification(id, customUserDetails)) {
            // 이전 group type, group id 는 그대로 적용
            PostEntity originalPost = postRepository.findById(id).get();
            postRequestDTO.setGroupId(originalPost.getGroupId());
            postRequestDTO.setGroupType(originalPost.getGroupType());

            PostEntity updatedPost = postService.updatePost(id, postRequestDTO);

            if (updatedPost == null) {
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Post/CannotFindId", "the result of updating post is null");
            }

            return ResponseUtil.createSuccessResponse(modelMapper.map(updatedPost, PostResponseDTO.class), HttpStatus.OK);
        } else {
            return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Post/PermissionDenied", "the user does not have permission to update this post");
        }
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ServerResponseDTO> deletePost(@PathVariable Long id,
                                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (postRepository.findById(id).isEmpty())
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Post/CannotFindId", "post with that id not found");

        if (postService.identification(id, customUserDetails)) {
            postService.deletePost(id);
            return ResponseUtil.createSuccessResponse("Post deleted successfully", HttpStatus.NO_CONTENT);
        } else {
            return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Post/PermissionDenied", "the user does not have permission to delete this post");
        }
    }
}
