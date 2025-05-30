package com.example.comitserver.controller;

import com.example.comitserver.dto.*;
import com.example.comitserver.entity.*;
import com.example.comitserver.entity.enumeration.JoinState;
import com.example.comitserver.repository.CreatedEventRepository;
import com.example.comitserver.repository.CreatedStudyRepository;
import com.example.comitserver.repository.EventRepository;
import com.example.comitserver.service.EventService;
import com.example.comitserver.service.UserService;
import com.example.comitserver.utils.ResponseUtil;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CreatedEventRepository createdEventRepository;

    @Autowired
    public EventController(EventService eventService, ModelMapper modelMapper, EventRepository eventRepository, UserService userService, CreatedEventRepository createdEventRepository) {
        this.eventService = eventService;
        this.modelMapper = modelMapper;
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.createdEventRepository = createdEventRepository;
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
    public ResponseEntity<ServerResponseDTO> postEvent(@RequestBody EventRequestDTO eventRequestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        UserEntity user = userService.getUserProfile(customUserDetails.getUserId());
        if (!user.getIsStaff()) return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Event/NotStaff", "the user is not a staff member");
        if (!eventService.checkDate(eventRequestDTO.getStartDate(),eventRequestDTO.getEndDate())){
            return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Event/InvalidDate", "the startDate and endDate is always future and endDate should be after startDate");
        }

        if (eventService.isDuplicateEvent(eventRequestDTO)) {
            return ResponseUtil.createErrorResponse(HttpStatus.CONFLICT, "Event/Duplicate", "An event with the same title, date, and location already exists.");
        }


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
                                                      @RequestBody EventRequestDTO eventRequestDTO, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserEntity user = userService.getUserProfile(customUserDetails.getUserId());
        if (!user.getIsStaff()) return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Event/NotStaff", "the user is not a staff member");

        if (eventRepository.findById(id).isEmpty())
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "event with that id not found");
        eventService.updateEventRecruitment(id);


        EventEntity updatedEvent = eventService.updateEvent(id, eventRequestDTO);

        if (updatedEvent == null) {
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "the result of updating event is null");
            //return ResponseEntity.notFound().build();
        }

        return ResponseUtil.createSuccessResponse(modelMapper.map(updatedEvent, EventResponseDTO.class), HttpStatus.OK);
        //return ResponseEntity.ok(modelMapper.map(updatedStudy, StudyResponseDTO.class));

        //ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PatchMapping("/events/{id}")
    public ResponseEntity<ServerResponseDTO> patchIsRecruiting(@PathVariable Long id, @RequestBody Map<String, Boolean> body, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserEntity user = userService.getUserProfile(customUserDetails.getUserId());
        if (!user.getIsStaff()) return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Event/NotStaff", "the user is not a staff member");

        if (eventRepository.findById(id).isEmpty()) return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "event with that id not found");
        eventService.updateEventRecruitment(id);

        EventEntity event = eventService.showEvent(id);
        event.setIsRecruiting(body.get("isRecruiting"));
        eventRepository.save(event);

        Map<String, Boolean> isRecruiting = new HashMap<>();
        isRecruiting.put("isRecruiting", event.getIsRecruiting());

        return ResponseUtil.createSuccessResponse(isRecruiting, HttpStatus.OK);

    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<ServerResponseDTO> deleteEvent(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserEntity user = userService.getUserProfile(customUserDetails.getUserId());
        if (!user.getIsStaff()) return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Event/NotStaff", "the user is not a staff member");

        if (eventRepository.findById(id).isEmpty()) return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "event with that id not found");
        eventService.updateEventRecruitment(id);

        EventEntity deletedEvent = eventService.showEvent(id);
        EventResponseDTO eventResponseDTO = modelMapper.map(deletedEvent, EventResponseDTO.class);

        eventService.deleteEvent(id);

        return ResponseUtil.createSuccessResponse(eventResponseDTO, HttpStatus.OK);
        //return ResponseEntity.ok(studyResponseDTO);

    }

    @GetMapping("/events/{id}/members")
    public ResponseEntity<ServerResponseDTO> getEventMembers(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(name= "state", required = true) JoinState state) {
        UserEntity user = userService.getUserProfile(customUserDetails.getUserId());

        if(!user.getIsStaff()) return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Event/NotStaff", "the user is not a staff member");

        if (eventRepository.findById(id).isEmpty()) return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "event with that id not found");
        eventService.updateEventRecruitment(id);

        List<CreatedEventEntity> createdEventEntities = eventService.getCreatedEventEntityByJoinState(id,state);

        List<UserResponseDTO> userDTOs = createdEventEntities.stream().map(CreatedEventEntity::getUser).map(userEntity -> modelMapper.map(userEntity, UserResponseDTO.class)).toList();

        return ResponseUtil.createSuccessResponse(userDTOs, HttpStatus.OK);

    }

    @PatchMapping("/events/{id}/{memberId}")
    public ResponseEntity<ServerResponseDTO> patchState(@PathVariable Long id, @PathVariable Long memberId, @RequestBody Map<String, String> body, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserEntity user = userService.getUserProfile(customUserDetails.getUserId());

        if (!user.getIsStaff()) return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Event/NotStaff", "the user is not a staff member");

        // 해당 이벤트에 상태 수정하려는 멤버 아이디가 존재하지 않는다면 에러
        if(!createdEventRepository.existsByEventIdAndUserId(id, memberId)) return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "event join request with that member id not found");

        Optional<CreatedEventEntity> createdEvent = createdEventRepository.findByEventIdAndUserId(id,memberId);

        CreatedEventEntity entity = createdEvent.get();

        // 이벤트 가입 요청 상태가 Wait가 아니라면 에러
        if (!entity.getState().equals(JoinState.Wait)) return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "EventJoin/JoinState", "event join request must be Wait");

        String joinStateStr = body.get("joinState");
        JoinState newState;
        try {
            newState = JoinState.valueOf(joinStateStr); // 문자열 → enum
        } catch (IllegalArgumentException e) {
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "InvalidJoinState", "joinState must be Accept or Reject");
        }

        entity.setState(newState);
        createdEventRepository.save(entity);

        Map<String, String> joinState = new HashMap<>();
        joinState.put("joinState", entity.getState().toString());
        return ResponseUtil.createSuccessResponse(joinState, HttpStatus.OK);

    }

    @PostMapping("/events/{id}/join")
    public ResponseEntity<ServerResponseDTO> joinEvent(@PathVariable Long id,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if (eventRepository.findById(id).isEmpty()) return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "event with that id not found");

        //이벤트가 마감되었거나, 이미 가입신청/가입완료인 경우 에러
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
        else return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Event/cannotJoin", "the user cannot be joined");
    }


    @DeleteMapping("/events/{id}/leave")
    public ResponseEntity<ServerResponseDTO> leaveEvent(@PathVariable Long id,
                                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // 스터디가 존재하지 않을 경우 처리
        if (eventRepository.findById(id).isEmpty()) {
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Event/CannotFindId", "event with that id not found");
        }

        eventService.updateEventRecruitment(id);
        if (!eventService.showEvent(id).getIsRecruiting()){
            return ResponseUtil.createErrorResponse(HttpStatus.FORBIDDEN, "Event/AlreadyEnd", "the event with that id already started or ended");
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
