package com.example.comitserver.service;

import com.example.comitserver.dto.ReservationRequestDTO;
import com.example.comitserver.dto.ReservationResponseDTO;
import com.example.comitserver.dto.UserResponseDTO;
import com.example.comitserver.entity.ReservationEntity;
import com.example.comitserver.entity.UserEntity;
import com.example.comitserver.entity.enumeration.Verification;
import com.example.comitserver.repository.ReservationRepository;
import com.example.comitserver.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    // 중복 예약 체크
    public boolean isOverlapping(LocalDateTime start, LocalDateTime end, Long exceptId) {
        long count = reservationRepository.countOverlappingReservations(start, end);
        if (exceptId != null) {
            // 수정 시 자기 자신 제외
            ReservationEntity except = reservationRepository.findById(exceptId).orElse(null);
            if (except != null && except.getStartTime().equals(start) && except.getEndTime().equals(end)) {
                return count > 1;
            }
        }
        return count > 0;
    }

    // 예약 생성
    public ReservationResponseDTO createReservation(ReservationRequestDTO dto, Long reserverId) {
        if (isOverlapping(dto.getStartTime(), dto.getEndTime(), null)) {
            throw new IllegalStateException("Reservation time conflict.");
        }
        ReservationEntity entity = new ReservationEntity();
        entity.setStudyId(dto.getStudyId());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setIsVerified(Verification.WAIT);
        UserEntity reserver = userRepository.findById(reserverId).orElseThrow(() -> new NoSuchElementException("User not found with id: " + reserverId));
        entity.setReserver(reserver);
        ReservationEntity saved = reservationRepository.save(entity);
        return toResponseDTO(saved);
    }

    // 예약 수정
    public ReservationResponseDTO updateReservation(Long id, ReservationRequestDTO dto, Long reserverId) {
        ReservationEntity entity = reservationRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Reservation not found with id: " + id));
        if (!entity.getReserver().getId().equals(reserverId)) throw new SecurityException("You do not have permission to update this reservation.");
        if (entity.getIsVerified() != Verification.WAIT) throw new IllegalStateException("Reservation can only be updated in WAIT status.");
        if (isOverlapping(dto.getStartTime(), dto.getEndTime(), id)) {
            throw new IllegalStateException("Reservation time conflict.");
        }
        entity.setStudyId(dto.getStudyId());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        ReservationEntity saved = reservationRepository.save(entity);
        return toResponseDTO(saved);
    }

    // 예약 삭제
    public void deleteReservation(Long id, Long reserverId, boolean isAdmin) {
        ReservationEntity entity = reservationRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Reservation not found with id: " + id));
        if (!isAdmin && !entity.getReserver().getId().equals(reserverId)) throw new SecurityException("You do not have permission to delete this reservation.");
        reservationRepository.delete(entity);
    }

    // 월별 예약 전체 조회
    public List<ReservationResponseDTO> getReservationsByMonth(Integer year, Integer month) {
        LocalDateTime start, end;
        if (year != null && month != null) {
            start = LocalDateTime.of(LocalDate.of(year, month, 1), LocalTime.MIN);
            end = start.plusMonths(1);
        } else {
            LocalDate now = LocalDate.now();
            start = LocalDateTime.of(now.withDayOfMonth(1), LocalTime.MIN);
            end = start.plusMonths(1);
        }
        List<ReservationEntity> list = reservationRepository.findByStartTimeBetween(start, end);
        return list.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    // 내 예약 월별 조회
    public List<ReservationResponseDTO> getMyReservations(Long reserverId, Integer year, Integer month) {
        LocalDateTime start, end;
        if (year != null && month != null) {
            start = LocalDateTime.of(LocalDate.of(year, month, 1), LocalTime.MIN);
            end = start.plusMonths(1);
        } else {
            LocalDate now = LocalDate.now();
            start = LocalDateTime.of(now.withDayOfMonth(1), LocalTime.MIN);
            end = start.plusMonths(1);
        }
        List<ReservationEntity> list = reservationRepository.findByReserverId(reserverId).stream()
                .filter(r -> !r.getStartTime().isBefore(start) && r.getStartTime().isBefore(end))
                .collect(Collectors.toList());
        return list.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    // 예약 승인
    public ReservationResponseDTO acceptReservation(Long id) {
        ReservationEntity entity = reservationRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Reservation not found with id: " + id));
        entity.setIsVerified(Verification.ACCEPT);
        ReservationEntity saved = reservationRepository.save(entity);
        return toResponseDTO(saved);
    }

    // 예약 거절
    public ReservationResponseDTO rejectReservation(Long id) {
        ReservationEntity entity = reservationRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Reservation not found with id: " + id));
        entity.setIsVerified(Verification.DECLINE);
        ReservationEntity saved = reservationRepository.save(entity);
        return toResponseDTO(saved);
    }

    // Entity -> ResponseDTO 변환
    private ReservationResponseDTO toResponseDTO(ReservationEntity entity) {
        ReservationResponseDTO dto = modelMapper.map(entity, ReservationResponseDTO.class);
        // reserver(UserEntity) -> UserResponseDTO 변환 보장
        if (entity.getReserver() != null) {
            dto.setReserver(modelMapper.map(entity.getReserver(), UserResponseDTO.class));
        }
        return dto;
    }
} 