package com.example.comitserver.controller;

import com.example.comitserver.dto.*;
import com.example.comitserver.entity.EventEntity;
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
import java.util.List;
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

    @PostMapping("/events")
    public ResponseEntity<ServerResponseDTO> postEvent(@RequestBody EventRequestDTO eventRequestDTO) {
        EventEntity newEvent = eventService.createEvent(eventRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest() // 현재 요청한 전체 URI(/api/events) 기준으로
                .path("/{id}") // /{id} 추가
                .buildAndExpand(newEvent.getId())
                .toUri();

        return ResponseUtil.createSuccessResponse(modelMapper.map(newEvent, EventResponseDTO.class), HttpStatus.CREATED, location);
        //return ResponseEntity.created(location).body(modelMapper.map(newStudy, StudyResponseDTO.class));
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<ServerResponseDTO> putEvent(@PathVariable Long id,
                                                      @RequestBody EventRequestDTO eventRequestDTO) {
        if (eventRepository.findById(id).isEmpty())
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "event with that id not found");


        EventEntity updatedEvent = eventService.updateEvent(id, eventRequestDTO);

        if (updatedEvent == null) {
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "the result of updating event is null");
            //return ResponseEntity.notFound().build();
        }

        return ResponseUtil.createSuccessResponse(modelMapper.map(updatedEvent, EventResponseDTO.class), HttpStatus.OK);
        //return ResponseEntity.ok(modelMapper.map(updatedStudy, StudyResponseDTO.class));

        //ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
