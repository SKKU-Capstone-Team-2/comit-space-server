package com.example.comitserver.repository;

import com.example.comitserver.entity.CreatedEventEntity;
import com.example.comitserver.entity.CreatedStudyEntity;
import com.example.comitserver.entity.StudyEntity;
import com.example.comitserver.entity.UserEntity;
import com.example.comitserver.entity.enumeration.JoinState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreatedStudyRepository extends JpaRepository<CreatedStudyEntity, Long> {
    List<CreatedStudyEntity> findByUser(UserEntity user);
    void deleteAllByStudyId(Long studyId);


    void deleteByUserId(Long userId);

    boolean existsByStudyIdAndUserId(Long studyId, Long requesterId);
    Optional<CreatedStudyEntity> findByStudyIdAndUserId(Long studyId, Long requesterId);

    List<CreatedStudyEntity> findByStudyIdAndState(Long studyId, JoinState state);
    List<CreatedStudyEntity> findByUserIdAndStateAndIsLeader(Long userId, JoinState state, boolean isLeader);
    List<CreatedStudyEntity> findByUserIdAndIsLeader(Long userId, boolean isLeader);
    boolean existsByStudyIdAndUserIdAndState(Long studyId, Long userId, JoinState state);
}