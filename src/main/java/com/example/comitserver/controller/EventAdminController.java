package com.example.comitserver.controller;

import com.example.comitserver.dto.*;
import com.example.comitserver.entity.EventEntity;
import com.example.comitserver.entity.StudyEntity;
import com.example.comitserver.repository.EventRepository;
import com.example.comitserver.service.EventService;
import com.example.comitserver.utils.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class EventAdminController {

    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;

    @Autowired
    public EventAdminController(EventService eventService, ModelMapper modelMapper, EventRepository eventRepository) {
        this.eventService = eventService;
        this.modelMapper = modelMapper;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/events")
    public ResponseEntity<ServerResponseDTO> getEvents() {
        List<EventEntity> allEvents = eventService.showAllEvents();
        List<EventResponseDTO> allEventsDTO = allEvents.stream()
                .map(entity -> modelMapper.map(entity, EventResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseUtil.createSuccessResponse(allEventsDTO, HttpStatus.OK);
        //return ResponseEntity.ok(allStudiesDTO);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<ServerResponseDTO> getEventById(@PathVariable Long id) {
        if (eventRepository.findById(id).isEmpty())
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "Event with that id not found");

        EventEntity event = eventService.showEvent(id);

        return ResponseUtil.createSuccessResponse(modelMapper.map(event, EventResponseDTO.class), HttpStatus.OK);
        //return ResponseEntity.ok(modelMapper.map(study, StudyResponseDTO.class));
    }

    @PatchMapping("/events/{id}")
    public ResponseEntity<ServerResponseDTO> patchIsRecruiting(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        if (eventRepository.findById(id).isEmpty()) return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "event with that id not found");

        EventEntity event = eventService.showEvent(id);
        event.setIsRecruiting(body.get("isRecruiting"));
        eventRepository.save(event);

        Map<String, Boolean> isRecruiting = new HashMap<>();
        isRecruiting.put("isRecruiting", event.getIsRecruiting());

        return ResponseUtil.createSuccessResponse(isRecruiting, HttpStatus.OK);

    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<ServerResponseDTO> deleteEvent(@PathVariable Long id) {
        if (eventRepository.findById(id).isEmpty()) return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "event with that id not found");

        EventEntity deletedEvent = eventService.showEvent(id);
        EventResponseDTO eventResponseDTO = modelMapper.map(deletedEvent, EventResponseDTO.class);

        eventService.deleteEvent(id);

        return ResponseUtil.createSuccessResponse(eventResponseDTO, HttpStatus.OK);
        //return ResponseEntity.ok(studyResponseDTO);

    }

}
