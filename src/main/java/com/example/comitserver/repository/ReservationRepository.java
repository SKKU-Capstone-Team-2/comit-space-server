package com.example.comitserver.repository;

import com.example.comitserver.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    @Query("SELECT COUNT(r) FROM ReservationEntity r WHERE r.startTime < :endTime AND r.endTime > :startTime")
    long countOverlappingReservations(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    List<ReservationEntity> findByReserverId(Long reserverId);
    List<ReservationEntity> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
} 