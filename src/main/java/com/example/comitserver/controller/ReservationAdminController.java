package com.example.comitserver.controller;

import com.example.comitserver.dto.ServerResponseDTO;
import com.example.comitserver.dto.ReservationResponseDTO;
import com.example.comitserver.service.ReservationService;
import com.example.comitserver.utils.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class ReservationAdminController {

    private final ReservationService reservationService;
    private final ModelMapper modelMapper;

    @Autowired
    public ReservationAdminController(ReservationService reservationService, ModelMapper modelMapper) {
        this.reservationService = reservationService;
        this.modelMapper = modelMapper;
    }

    @PatchMapping("/reservations/{id}/accept")
    public ResponseEntity<ServerResponseDTO> acceptReservation(@PathVariable Long id) {
        try {
            ReservationResponseDTO dto = reservationService.acceptReservation(id);
            return ResponseUtil.createSuccessResponse(dto, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "reservation/error", e.getMessage());
        }
    }

    @PatchMapping("/reservations/{id}/reject")
    public ResponseEntity<ServerResponseDTO> rejectReservation(@PathVariable Long id) {
        try {
            ReservationResponseDTO dto = reservationService.rejectReservation(id);
            return ResponseUtil.createSuccessResponse(dto, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "reservation/error", e.getMessage());
        }
    }
} 