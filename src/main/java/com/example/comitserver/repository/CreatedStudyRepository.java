package com.example.comitserver.repository;

import com.example.comitserver.entity.CreatedStudyEntity;
import com.example.comitserver.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreatedStudyRepository extends JpaRepository<CreatedStudyEntity, Long> {
    List<CreatedStudyEntity> findByUser(UserEntity user);
    void deleteAllByStudyId(Long studyId);

    void deleteByStudyId(Long studyId);

    void deleteByUserId(Long userId);

    boolean existsByStudyIdAndUserId(Long studyId, Long requesterId);
    Optional<CreatedStudyEntity> findByStudyIdAndUserId(Long studyId, Long requesterId);
}