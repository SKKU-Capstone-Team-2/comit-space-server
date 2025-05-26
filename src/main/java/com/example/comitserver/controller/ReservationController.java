package com.example.comitserver.controller;

import com.example.comitserver.dto.ReservationRequestDTO;
import com.example.comitserver.dto.ReservationResponseDTO;
import com.example.comitserver.dto.ServerResponseDTO;
import com.example.comitserver.service.ReservationService;
import com.example.comitserver.utils.ResponseUtil;
import com.example.comitserver.dto.CustomUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ModelMapper modelMapper;

    // 월별 전체 예약 조회
    @GetMapping("/reservations")
    public ResponseEntity<ServerResponseDTO> getReservations(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<ReservationResponseDTO> reservations = reservationService.getReservationsByMonth(year, month);
        return ResponseUtil.createSuccessResponse(reservations, HttpStatus.OK);
    }

    // 내 예약 월별 조회
    @GetMapping("/reservations/my")
    public ResponseEntity<ServerResponseDTO> getMyReservations(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ReservationResponseDTO> reservations = reservationService.getMyReservations(userDetails.getUserId(), year, month);
        return ResponseUtil.createSuccessResponse(reservations, HttpStatus.OK);
    }

    // 예약 생성
    @PostMapping("/reservations")
    public ResponseEntity<ServerResponseDTO> createReservation(
            @RequestBody ReservationRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            ReservationResponseDTO entity = reservationService.createReservation(dto, userDetails.getUserId());
            return ResponseUtil.createSuccessResponse(entity, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseUtil.createErrorResponse(HttpStatus.CONFLICT, "reservation/conflict", e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "reservation/error", e.getMessage());
        }
    }

    // 예약 수정
    @PutMapping("/reservations/{id}")
    public ResponseEntity<ServerResponseDTO> updateReservation(
            @PathVariable Long id,
            @RequestBody ReservationRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            ReservationResponseDTO entity = reservationService.updateReservation(id, dto, userDetails.getUserId());
            return ResponseUtil.createSuccessResponse(entity, HttpStatus.OK);
        } catch (SecurityException e) {
            return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "reservation/permission", e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseUtil.createErrorResponse(HttpStatus.CONFLICT, "reservation/conflict", e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "reservation/error", e.getMessage());
        }
    }

    // 예약 삭제
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<ServerResponseDTO> deleteReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            reservationService.deleteReservation(id, userDetails.getUserId(), false);
            return ResponseUtil.createSuccessResponse("Deleted", HttpStatus.OK);
        } catch (SecurityException e) {
            return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "reservation/permission", e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "reservation/error", e.getMessage());
        }
    }
} 