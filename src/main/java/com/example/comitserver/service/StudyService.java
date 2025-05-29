package com.example.comitserver.service;

import com.example.comitserver.dto.CustomUserDetails;
import com.example.comitserver.dto.StudyRequestDTO;
import com.example.comitserver.dto.UserResponseDTO;
import com.example.comitserver.entity.CreatedStudyEntity;
import com.example.comitserver.entity.StudyEntity;
import com.example.comitserver.entity.UserEntity;
import com.example.comitserver.entity.enumeration.JoinState;
import com.example.comitserver.repository.CreatedStudyRepository;
import com.example.comitserver.repository.StudyRepository;
import com.example.comitserver.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional
public class StudyService {

    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final CreatedStudyRepository createdStudyRepository;

    public StudyService(StudyRepository studyRepository, UserRepository userRepository, CreatedStudyRepository createdStudyRepository) {
        this.studyRepository = studyRepository;
        this.userRepository = userRepository;
        this.createdStudyRepository = createdStudyRepository;
    }

    public List<StudyEntity> showAllStudies() {
        return studyRepository.findAll();
    }

    public StudyEntity showStudy(Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Study not found with id: " + id));
    }

    public StudyEntity createStudy(StudyRequestDTO studyRequestDTO, CustomUserDetails customUserDetails) {
        // Create a new study and assign the current user as mentor
        StudyEntity newStudy = new StudyEntity();

        UserEntity mentor = userRepository.findById(customUserDetails.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + customUserDetails.getUserId()));
        newStudy.setMentor(mentor);
        fillStudyFields(newStudy, studyRequestDTO);
        studyRepository.save(newStudy);

        CreatedStudyEntity createdStudy = new CreatedStudyEntity();
        createdStudy.setUser(mentor);
        createdStudy.setStudy(newStudy);
        createdStudy.setLeader(true);
        createdStudy.setState(JoinState.Accept);
        createdStudyRepository.save(createdStudy);

        return newStudy;
    }

    public StudyEntity updateStudy(Long id, StudyRequestDTO studyRequestDTO) {
        StudyEntity studyToUpdate = showStudy(id);

        fillStudyFields(studyToUpdate, studyRequestDTO);
        studyRepository.save(studyToUpdate);

        return studyToUpdate;
    }

    public void deleteStudy(Long id) {
        createdStudyRepository.deleteAllByStudyId(id);
        StudyEntity deletingStudy = showStudy(id);
        studyRepository.delete(deletingStudy);
    }

    //
    public Boolean identification(Long id, CustomUserDetails customUserDetails) {
        StudyEntity study = showStudy(id);
        Long mentorId = study.getMentor().getId();
        Long requesterId = customUserDetails.getUserId();
        return Objects.equals(requesterId, mentorId);
    }

    public Boolean checkStudyJoinAvailable(Long studyId, CustomUserDetails customUserDetails) {
        Long requesterId = customUserDetails.getUserId();
        boolean exists = createdStudyRepository.existsByStudyIdAndUserId(studyId, requesterId);

        return !exists;
    }

    public List<CreatedStudyEntity> getCreatedStudyEntityByJoinState(Long studyId, CustomUserDetails customUserDetails, JoinState joinState) {
        Long requesterId = customUserDetails.getUserId();

        return createdStudyRepository.findByStudyIdAndState(studyId, joinState);
    }

    public void joinStudy(Long studyId, CustomUserDetails customUserDetails) {

        UserEntity requestUser = userRepository.findById(customUserDetails.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + customUserDetails.getUserId()));

        CreatedStudyEntity createdStudy = new CreatedStudyEntity();
        createdStudy.setUser(requestUser);
        createdStudy.setStudy(showStudy(studyId));
        createdStudy.setLeader(false);
        createdStudy.setState(JoinState.Wait);
        createdStudyRepository.save(createdStudy);
    }

    public void leaveStudy(Long studyId, CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUserId();

        // 탈퇴 요청자의 가입 정보를 CreatedStudyEntity에서 찾음
        CreatedStudyEntity createdStudy = createdStudyRepository.findByStudyIdAndUserId(studyId, userId)
                .orElseThrow(() -> new NoSuchElementException("The user is not a member of this study"));

        // 스터디 리더는 스터디 탈퇴가 아닌 삭제만 가능
        if (createdStudy.isLeader()) {
            throw new IllegalStateException("Study leader cannot leave the study. please delete the study");
        }

        // 유효성 체크를 끝낸 후 해당 엔터티를 삭제
        createdStudyRepository.delete(createdStudy);
    }


    private void fillStudyFields(StudyEntity study, StudyRequestDTO dto) {
        // Fill in all study fields except for mentor
        study.setTitle(dto.getTitle());
        study.setImageSrc(dto.getImageSrc());
        study.setDay(dto.getDay());
        study.setStartTime(dto.getStartTime());
        study.setEndTime(dto.getEndTime());
        study.setLevel(dto.getLevel());
        study.setTags(dto.getTags());
        study.setCampus(dto.getCampus());
        study.setDescription(dto.getDescription());
        study.setIsRecruiting(dto.getIsRecruiting());
        study.setSemester(dto.getSemester());
        study.setYear(dto.getYear());
    }
}
