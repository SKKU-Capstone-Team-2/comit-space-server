package com.example.comitserver.controller;

import com.example.comitserver.dto.CustomUserDetails;
import com.example.comitserver.dto.ServerResponseDTO;
import com.example.comitserver.dto.StudyRequestDTO;
import com.example.comitserver.dto.StudyResponseDTO;
import com.example.comitserver.entity.StudyEntity;
import com.example.comitserver.repository.StudyRepository;
import com.example.comitserver.service.StudyService;
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
@RequestMapping("/api")
public class StudyController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final StudyRepository studyRepository;

    @Autowired
    public StudyController(StudyService studyService, ModelMapper modelMapper, StudyRepository studyRepository) {
        this.studyService = studyService;
        this.modelMapper = modelMapper;
        this.studyRepository = studyRepository;
    }

    @GetMapping("/studies")
    public ResponseEntity<ServerResponseDTO> getStudies() {
        List<StudyEntity> allStudies = studyService.showAllStudies();
        List<StudyResponseDTO> allStudiesDTO = allStudies.stream()
                .map(entity -> modelMapper.map(entity, StudyResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseUtil.createSuccessResponse(allStudiesDTO, HttpStatus.OK);
        //return ResponseEntity.ok(allStudiesDTO);
    }

    @GetMapping("/studies/{id}")
    public ResponseEntity<ServerResponseDTO> getStudyById(@PathVariable Long id) {
        if (studyRepository.findById(id).isEmpty())
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Study/CannotFindId", "study with that id not found");

        StudyEntity study = studyService.showStudy(id);

        return ResponseUtil.createSuccessResponse(modelMapper.map(study, StudyResponseDTO.class), HttpStatus.OK);
        //return ResponseEntity.ok(modelMapper.map(study, StudyResponseDTO.class));
    }

    @PostMapping("/studies")
    public ResponseEntity<ServerResponseDTO> postStudy(@RequestBody StudyRequestDTO studyRequestDTO,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        StudyEntity newStudy = studyService.createStudy(studyRequestDTO, customUserDetails);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest() // 현재 요청한 전체 URI(/api/studies) 기준으로
                .path("/{id}") // /{id} 추가
                .buildAndExpand(newStudy.getId())
                .toUri();

        return ResponseUtil.createSuccessResponse(modelMapper.map(newStudy, StudyResponseDTO.class), HttpStatus.CREATED, location);
        //return ResponseEntity.created(location).body(modelMapper.map(newStudy, StudyResponseDTO.class));
    }

    @PutMapping("/studies/{id}")
    public ResponseEntity<ServerResponseDTO> putStudy(@PathVariable Long id,
                                                      @RequestBody StudyRequestDTO studyRequestDTO,
                                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (studyRepository.findById(id).isEmpty())
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Study/CannotFindId", "study with that id not found");

        if (studyService.identification(id, customUserDetails)) {
            StudyEntity updatedStudy = studyService.updateStudy(id, studyRequestDTO);

            if (updatedStudy == null) {
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Study/CannotFindId", "the result of updating study is null");
                //return ResponseEntity.notFound().build();
            }

            return ResponseUtil.createSuccessResponse(modelMapper.map(updatedStudy, StudyResponseDTO.class), HttpStatus.OK);
            //return ResponseEntity.ok(modelMapper.map(updatedStudy, StudyResponseDTO.class));
        } else
            return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Study/PermissionDenied", "the user does not have permission to update this study");
        //ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/studies/{id}")
    public ResponseEntity<ServerResponseDTO> deleteStudy(@PathVariable Long id,
                                                         @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (studyRepository.findById(id).isEmpty()) return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Study/CannotFindId", "study with that id not found");

        if(studyService.identification(id, customUserDetails)) {
            StudyEntity deletedStudy = studyService.showStudy(id);
            StudyResponseDTO studyResponseDTO = modelMapper.map(deletedStudy, StudyResponseDTO.class);

            studyService.deleteStudy(id);

            return ResponseUtil.createSuccessResponse(studyResponseDTO, HttpStatus.OK);
            //return ResponseEntity.ok(studyResponseDTO);
        }
        else return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Study/PermissionDenied", "the user does not have permission to delete this study");
                //ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // 스터디 생성원이 아닌 다른 사용자가 스터디 가입
    @PostMapping("/studies/{id}/join")
    public ResponseEntity<ServerResponseDTO> joinStudy(@PathVariable Long id,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if (studyRepository.findById(id).isEmpty()) return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Study/CannotFindId", "study with that id not found");

        if(studyService.checkStudyJoinAvailable(id, customUserDetails)){
            studyService.joinStudy(id, customUserDetails);
            StudyEntity joinStudy = studyService.showStudy(id);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath() // 기본 경로를 root conte xt(/api)로 설정
                    .path("/studies/{id}") // custom 경로 설정
                    .buildAndExpand(joinStudy.getId())
                    .toUri();

            return ResponseUtil.createSuccessResponse(modelMapper.map(joinStudy, StudyResponseDTO.class), HttpStatus.CREATED, location);
            //return ResponseEntity.created(location).body(modelMapper.map(newStudy, StudyResponseDTO.class));

        }
        else return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Study/alreadyJoined", "the user is already in this study");
    }

    @DeleteMapping("/studies/{id}/leave")
    public ResponseEntity<ServerResponseDTO> leaveStudy(@PathVariable Long id,
                                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // 스터디가 존재하지 않을 경우 처리
        if (studyRepository.findById(id).isEmpty()) {
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Study/CannotFindId", "study with that id not found");
        }

        // 요청자가 해당 스터디에 가입되어 있는지 확인
        boolean isMember = studyService.checkStudyJoinAvailable(id, customUserDetails); // 반대 동작을 재사용

        if (!isMember) { // 스터디에 가입되어 있는 경우
            studyService.leaveStudy(id, customUserDetails);
            return ResponseUtil.createSuccessResponse("Successfully left the study", HttpStatus.OK);
        } else {
            return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Study/NotJoined", "the user is not a member of this study");
        }
    }



}
