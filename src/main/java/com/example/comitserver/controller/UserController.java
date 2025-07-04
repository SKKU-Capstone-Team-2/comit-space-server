package com.example.comitserver.controller;

import com.example.comitserver.dto.*;
import com.example.comitserver.entity.*;
import com.example.comitserver.service.UserService;
import com.example.comitserver.utils.ResponseUtil;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/staffs")
    public ResponseEntity<?> getAllStaffProfiles() {
        List<UserEntity> users = userService.getAllUsersByStaffStatus(true);
        List<UserResponseDTO> userDTOs = users.stream().map(entity -> modelMapper.map(entity, UserResponseDTO.class)).toList();
        return ResponseUtil.createSuccessResponse(userDTOs, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getUserId();
        UserEntity user = userService.getUserProfile(userId);
        UserResponseDTO userDTO = modelMapper.map(user, UserResponseDTO.class);
        return ResponseUtil.createSuccessResponse(userDTO, HttpStatus.OK);
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid UserRequestDTO userDTO) {
        Long userId = ((CustomUserDetails) userDetails).getUserId();
        UserEntity user = userService.getUserProfile(userId);
        userService.updateUserProfile(userId, userDTO);
        UserResponseDTO updatedUserDTO = modelMapper.map(user, UserResponseDTO.class);
        return ResponseUtil.createSuccessResponse(updatedUserDTO, HttpStatus.OK);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getUserId();
        UserEntity user = userService.getUserProfile(userId);
        boolean deleted = userService.deleteUser(userId);

        // 삭제 실패 시, Internal Server Error 응답 반환
        if (!deleted) {
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Deletion Failed", "Failed to delete user with id: " + userId);
        } else {
            // 삭제 성공 시, OK 응답 반환
            UserResponseDTO userDTO = modelMapper.map(user, UserResponseDTO.class);
            return ResponseUtil.createSuccessResponse(userDTO, HttpStatus.OK);
        }
    }

    @GetMapping("/profile/created-studies")
    public ResponseEntity<?> getCreatedStudy(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getUserId();
        List<StudyEntity> createdStudies = userService.getCreatedStudies(userId);
        List<StudyResponseDTO> studyDTOs = createdStudies.stream().map(entity -> modelMapper.map(entity, StudyResponseDTO.class)).toList();
        return ResponseUtil.createSuccessResponse(studyDTOs, HttpStatus.OK);
    }

    @GetMapping("/profile/joined-studies")
    public ResponseEntity<?> getJoinedStudy(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getUserId();
        List<CreatedStudyEntity> joinedStudies = userService.getJoinedStudies(userId);

        List<StudyWithStateResponseDTO> studyDTOs = joinedStudies.stream().map(joinedStudy -> {
            StudyResponseDTO studyDTO = modelMapper.map(joinedStudy.getStudy(), StudyResponseDTO.class);
            StudyWithStateResponseDTO dto = new StudyWithStateResponseDTO();
            dto.setStudy(studyDTO);
            dto.setState(joinedStudy.getState().toString());
            return dto;
        }).toList();
        return ResponseUtil.createSuccessResponse(studyDTOs, HttpStatus.OK);
    }

    @GetMapping("/profile/joined-events")
    public ResponseEntity<?> getJoinedEvent(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getUserId();
        List<CreatedEventEntity> joinedEvents = userService.getJoinedEvents(userId);

        List<EventWithStateResponseDTO> eventDTOs = joinedEvents.stream().map(joinedEvent -> {
            EventResponseDTO eventDTO = modelMapper.map(joinedEvent.getEvent(), EventResponseDTO.class);
            EventWithStateResponseDTO dto = new EventWithStateResponseDTO();
            dto.setEvent(eventDTO);
            dto.setState(joinedEvent.getState().toString());
            return dto;
        }).toList();
        return ResponseUtil.createSuccessResponse(eventDTOs, HttpStatus.OK);
    }
}