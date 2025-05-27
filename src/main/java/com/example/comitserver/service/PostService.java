package com.example.comitserver.service;


import com.example.comitserver.dto.CustomUserDetails;
import com.example.comitserver.dto.PostRequestDTO;
import com.example.comitserver.entity.PostEntity;
import com.example.comitserver.entity.UserEntity;
import com.example.comitserver.entity.enumeration.GroupType;
import com.example.comitserver.repository.PostRepository;
import com.example.comitserver.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<PostEntity> showAllPosts() {
        return postRepository.findAll();
    }

    public PostEntity showPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("Post not found with id: " + id));
    }

    public List<PostEntity> showPostsByGroup(Long groupId, GroupType groupType) {
        return postRepository.findByGroupIdAndGroupType(groupId, groupType);
    }

    public PostEntity createPost(PostRequestDTO postRequestDTO, CustomUserDetails customUserDetails) {
        PostEntity postEntity = new PostEntity();

        UserEntity author = userRepository.findById(customUserDetails.getUserId())
                .orElseThrow(()-> new NoSuchElementException("User not found with id: " + customUserDetails.getUserId()));
        postEntity.setAuthor(author);
        postEntity.setGroupId(postRequestDTO.getGroupId());
        postEntity.setGroupType(postRequestDTO.getGroupType());
        postEntity.setTitle(postRequestDTO.getTitle());
        postEntity.setContent(postRequestDTO.getContent());
        postEntity.setImageSrc(postRequestDTO.getImageSrc());
        postRepository.save(postEntity);

        return postEntity;
    }

    public PostEntity updatePost(Long id, PostRequestDTO postRequestDTO) {
        PostEntity postToUpdate = showPost(id);

        postToUpdate.setGroupId(postRequestDTO.getGroupId());
        postToUpdate.setGroupType(postRequestDTO.getGroupType());
        postToUpdate.setTitle(postRequestDTO.getTitle());
        postToUpdate.setContent(postRequestDTO.getContent());
        postToUpdate.setImageSrc(postRequestDTO.getImageSrc());
        postRepository.save(postToUpdate);

        return postToUpdate;
    }

    public void deletePost(Long id) {
        PostEntity postToDelete = showPost(id);
        postRepository.delete(postToDelete);
    }

    public Boolean identification(Long id, CustomUserDetails customUserDetails) {
        PostEntity post = showPost(id);
        Long authorId = post.getAuthor().getId();
        Long requesterId = customUserDetails.getUserId();
        return Objects.equals(requesterId, authorId);
    }

}
