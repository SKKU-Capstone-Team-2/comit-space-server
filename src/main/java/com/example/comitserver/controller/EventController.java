package com.example.comitserver.controller;

import com.example.comitserver.dto.*;
import com.example.comitserver.entity.*;
import com.example.comitserver.entity.enumeration.JoinState;
import com.example.comitserver.repository.EventRepository;
import com.example.comitserver.service.EventService;
import com.example.comitserver.service.UserService;
import com.example.comitserver.utils.ResponseUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;
    private final UserService userService;

    @Autowired
    public EventController(EventService eventService, ModelMapper modelMapper, EventRepository eventRepository, UserService userService) {
        this.eventService = eventService;
        this.modelMapper = modelMapper;
        this.eventRepository = eventRepository;
        this.userService = userService;
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

    @GetMapping("/events/{id}/members")
    public ResponseEntity<ServerResponseDTO> getEventMembers(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(name= "state", required = true) JoinState state) {
        UserEntity user = userService.getUserProfile(customUserDetails.getUserId());

        if(!user.getIsStaff()) return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Event/NotStaff", "the user is not a staff member");

        List<CreatedEventEntity> createdEventEntities = eventService.getCreatedEventEntityByJoinState(id,state);

        List<UserResponseDTO> userDTOs = createdEventEntities.stream().map(CreatedEventEntity::getUser).map(userEntity -> modelMapper.map(userEntity, UserResponseDTO.class)).toList();

        return ResponseUtil.createSuccessResponse(userDTOs, HttpStatus.OK);
        

    }

    @PostMapping("/events/{id}/join")
    public ResponseEntity<ServerResponseDTO> joinEvent(@PathVariable Long id,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if (eventRepository.findById(id).isEmpty()) return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "event with that id not found");

        if(eventService.checkEventJoinAvailable(id, customUserDetails)){
            eventService.joinEvent(id, customUserDetails);
            EventEntity joinEvent = eventService.showEvent(id);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath() // 기본 경로를 root conte xt(/api)로 설정
                    .path("/events/{id}") // custom 경로 설정
                    .buildAndExpand(joinEvent.getId())
                    .toUri();

            return ResponseUtil.createSuccessResponse(modelMapper.map(joinEvent, EventResponseDTO.class), HttpStatus.CREATED, location);
            //return ResponseEntity.created(location).body(modelMapper.map(newStudy, StudyResponseDTO.class));

        }
        else return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Event/alreadyJoined", "the user is already in this event");
    }


    @DeleteMapping("/events/{id}/leave")
    public ResponseEntity<ServerResponseDTO> leaveEvent(@PathVariable Long id,
                                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // 스터디가 존재하지 않을 경우 처리
        if (eventRepository.findById(id).isEmpty()) {
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "event with that id not found");
        }

        // 요청자가 해당 스터디에 가입되어 있는지 확인
        boolean isMember = eventService.checkEventJoinAvailable(id, customUserDetails); // 반대 동작을 재사용

        if (!isMember) { // 스터디에 가입되어 있는 경우
            eventService.leaveEvent(id, customUserDetails);
            return ResponseUtil.createSuccessResponse("Successfully left the event", HttpStatus.OK);
        } else {
            return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Event/NotJoined", "the user is not a member of this event");
        }
    }

}
