package com.example.comitserver.repository;

import com.example.comitserver.entity.CreatedEventEntity;
import com.example.comitserver.entity.CreatedStudyEntity;
import com.example.comitserver.entity.UserEntity;
import com.example.comitserver.entity.enumeration.JoinState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreatedEventRepository extends JpaRepository<CreatedEventEntity, Long> {
    List<CreatedEventEntity> findByUser(UserEntity user);
    void deleteAllByEventId(Long eventId);

    void deleteByEventId(Long eventId);

    void deleteByUserId(Long userId);

    boolean existsByEventIdAndUserId(Long eventId, Long requesterId);
    Optional<CreatedEventEntity> findByEventIdAndUserId(Long eventId, Long requesterId);
    List<CreatedEventEntity> findByEventIdAndState(Long eventId, JoinState state);
    List<CreatedEventEntity> findByUserIdAndState(Long userId, JoinState state);
    boolean existsByEventIdAndUserIdAndState(Long eventId, Long userId, JoinState state);
}