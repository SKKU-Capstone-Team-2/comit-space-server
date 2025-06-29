package com.example.comitserver.service;

import com.example.comitserver.dto.UserRequestDTO;
import com.example.comitserver.entity.*;
import com.example.comitserver.entity.enumeration.JoinState;
import com.example.comitserver.exception.DuplicateResourceException;
import com.example.comitserver.exception.ResourceNotFoundException;
import com.example.comitserver.repository.CreatedEventRepository;
import com.example.comitserver.repository.CreatedStudyRepository;
import com.example.comitserver.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CreatedStudyRepository createdStudyRepository;
    private final CreatedEventRepository createdEventRepository;

    @Autowired
    public UserService(UserRepository userRepository, CreatedStudyRepository createdStudyRepository, CreatedEventRepository createdEventRepository) {
        this.userRepository = userRepository;
        this.createdStudyRepository = createdStudyRepository;
        this.createdEventRepository = createdEventRepository;
    }

    public List<UserEntity> getAllUsersByStaffStatus(Boolean isStaff) {
        return userRepository.findByIsStaff(isStaff);
    }

    public UserEntity getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public void updateUserProfile(Long userId, @Valid UserRequestDTO userDTO) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // @Valid 가 UserDto의 필드 validity check
        checkForDuplicateFields(userDTO, user);
        updateUserFields(user, userDTO);

        userRepository.save(user);
    }

    private void checkForDuplicateFields(UserRequestDTO userDTO, UserEntity user) {
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateResourceException("Email is already in use.");
        }

        if (userDTO.getStudentId() != null && !userDTO.getStudentId().equals(user.getStudentId()) &&
                userRepository.existsByStudentId(userDTO.getStudentId())) {
            throw new DuplicateResourceException("Student ID is already in use.");
        }

        if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().equals(user.getPhoneNumber()) &&
                userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number is already in use.");
        }
    }

    private void updateUserFields(UserEntity user, UserRequestDTO userDTO) {
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getStudentId() != null) {
            user.setStudentId(userDTO.getStudentId());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getBio() != null) {
            user.setBio(userDTO.getBio());
        }
        if (userDTO.getGithub() != null) {
            user.setGithub(userDTO.getGithub());
        }
        if (userDTO.getBlog() != null) {
            user.setBlog(userDTO.getBlog());
        }
        if (userDTO.getProfileImage() != null) {
            user.setProfileImage(userDTO.getProfileImage());
        }
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.existsById(Math.toIntExact(userId))) {
            userRepository.deleteById(Math.toIntExact(userId));
            return true;
        } else {
            return false;
        }
    }

    public List<StudyEntity> getCreatedStudies(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        List<CreatedStudyEntity> createdStudies = createdStudyRepository.findByUserIdAndIsLeader(userId,true);

        // Extract and return the list of StudyEntity objects from CreatedStudyEntity
        return createdStudies.stream()
                .map(CreatedStudyEntity::getStudy)
                .collect(Collectors.toList());
    }

    public List<CreatedStudyEntity> getJoinedStudies(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        return createdStudyRepository.findByUserIdAndIsLeader(userId, false);
    }

    public List<CreatedEventEntity> getJoinedEvents(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        return createdEventRepository.findByUserId(userId);
    }
}
