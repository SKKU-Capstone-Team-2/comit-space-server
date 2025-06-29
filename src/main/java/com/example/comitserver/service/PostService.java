package com.example.comitserver.service;


import com.example.comitserver.dto.CustomUserDetails;
import com.example.comitserver.dto.PostRequestDTO;
import com.example.comitserver.entity.PostEntity;
import com.example.comitserver.entity.UserEntity;
import com.example.comitserver.entity.enumeration.GroupType;
import com.example.comitserver.repository.PostRepository;
import com.example.comitserver.repository.UserRepository;
import com.example.comitserver.repository.CreatedStudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final CreatedStudyRepository createdStudyRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, CreatedStudyRepository createdStudyRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.createdStudyRepository = createdStudyRepository;
    }

    public List<PostEntity> showAllPosts() {
        return postRepository.findAll();
    }

    public PostEntity showPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("Post not found with id: " + id));
    }

    public List<PostEntity> showPostsByGroupType(GroupType groupType) {
        return postRepository.findByGroupType(groupType);
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

    public boolean checkUserInGroup(Long groupId, GroupType groupType, CustomUserDetails userDetails) {
        if (groupType == GroupType.STUDY) {
            return createdStudyRepository.existsByStudyIdAndUserId(groupId, userDetails.getUserId());
        } else if (groupType == GroupType.EVENT) {
            return userDetails.isStaff(); // event의 경우 운영진 여부로 판단
        }
        return false;
    }

    @Transactional
    public PostEntity incrementLike(Long id, CustomUserDetails customUserDetails) {
        UserEntity author = userRepository.findById(customUserDetails.getUserId())
                .orElseThrow(()-> new NoSuchElementException("User not found with id: " + customUserDetails.getUserId()));
        PostEntity post = showPost(id);
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
        return post;
    }

    @Transactional
    public PostEntity decrementLike(Long id, CustomUserDetails customUserDetails) {
        UserEntity author = userRepository.findById(customUserDetails.getUserId())
                .orElseThrow(()-> new NoSuchElementException("User not found with id: " + customUserDetails.getUserId()));
        PostEntity post = showPost( id);

        // 좋아요가 0 이하로 내려가지 않도록 체크
        if (post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
        }
        postRepository.save(post);
        return post;
    }

}
