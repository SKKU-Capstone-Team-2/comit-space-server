package com.example.comitserver.service;

import com.example.comitserver.dto.CommentDTO;
import com.example.comitserver.dto.CustomUserDetails;
import com.example.comitserver.entity.CommentEntity;
import com.example.comitserver.entity.UserEntity;
import com.example.comitserver.repository.CommentRepository;
import com.example.comitserver.repository.PostRepository;
import com.example.comitserver.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public CommentEntity showComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("Comment not found with id: " + id));
    }

    public CommentEntity createComment(CommentDTO commentDTO, CustomUserDetails customUserDetails) {
        CommentEntity commentEntity = new CommentEntity();

        UserEntity author = userRepository.findById(customUserDetails.getUserId())
                .orElseThrow(()-> new NoSuchElementException("User not found with id: " + customUserDetails.getUserId()));

        commentEntity.setUserId(author.getId());
        commentEntity.setPostId(commentDTO.getPostId());
        commentEntity.setContent(commentDTO.getContent());
        commentRepository.save(commentEntity);

        return commentEntity;
    }

    public CommentEntity updateComment(Long id, CommentDTO commentDTO) {
        CommentEntity commentToUpdate = showComment(id);

        commentToUpdate.setContent(commentDTO.getContent());
        commentRepository.save(commentToUpdate);

        return commentToUpdate;
    }

    public void deleteComment(Long id) {
        CommentEntity commentToDelete = showComment(id);
        commentRepository.deleteById(id);
    }

    public Boolean identification(Long id, CustomUserDetails customUserDetails) {
        CommentEntity comment = showComment(id);
        Long authorId = comment.getUserId();
        Long requesterId = customUserDetails.getUserId();
        return Objects.equals(requesterId, authorId);
    }

}
