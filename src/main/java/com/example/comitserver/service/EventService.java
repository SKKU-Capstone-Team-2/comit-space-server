package com.example.comitserver.service;

import com.example.comitserver.dto.CustomUserDetails;
import com.example.comitserver.dto.EventRequestDTO;
import com.example.comitserver.entity.*;
import com.example.comitserver.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final CreatedEventRepository createdEventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, CreatedEventRepository createdEventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.createdEventRepository = createdEventRepository;
        this.userRepository = userRepository;
    }

    public List<EventEntity> showAllEvents() {
        return eventRepository.findAll();
    }

    public EventEntity showEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));
    }

    public EventEntity createEvent(EventRequestDTO eventRequestDTO) {
        EventEntity newEvent = new EventEntity();

        fillEventFields(newEvent, eventRequestDTO);
        eventRepository.save(newEvent);

        return newEvent;
    }

    public EventEntity updateEvent(Long id, EventRequestDTO eventRequestDTO) {
        EventEntity eventToUpdate = showEvent(id);

        fillEventFields(eventToUpdate, eventRequestDTO);
        eventRepository.save(eventToUpdate);

        return eventToUpdate;
    }

    public void deleteEvent(Long id) {
        EventEntity deletingEvent = showEvent(id);
        eventRepository.delete(deletingEvent);
    }

    public Boolean checkEventJoinAvailable(Long eventId, CustomUserDetails customUserDetails) {
        Long requesterId = customUserDetails.getUserId();
        boolean exists = createdEventRepository.existsByEventIdAndUserId(eventId, requesterId);

        return !exists;
    }

    public void joinEvent(Long eventId, CustomUserDetails customUserDetails) {

        UserEntity requestUser = userRepository.findById(customUserDetails.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + customUserDetails.getUserId()));

        CreatedEventEntity createdEvent = new CreatedEventEntity();
        createdEvent.setUser(requestUser);
        createdEvent.setEvent(showEvent(eventId));
        createdEventRepository.save(createdEvent);
    }

    public void leaveEvent(Long eventId, CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUserId();

        CreatedEventEntity createdEvent = createdEventRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NoSuchElementException("The user is not a member of this event"));

        createdEventRepository.delete(createdEvent);
    }

    private void fillEventFields(EventEntity event, EventRequestDTO dto) {
        event.setTitle(dto.getTitle());
        event.setImageSrc(dto.getImageSrc());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setTags(dto.getTags());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setIsRecruiting(dto.getIsRecruiting());
        event.setSemester(dto.getSemester());
        event.setYear(dto.getYear());
    }
}
