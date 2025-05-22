package com.example.comitserver.service;

import com.example.comitserver.dto.CustomUserDetails;
import com.example.comitserver.dto.EventRequestDTO;
import com.example.comitserver.dto.StudyRequestDTO;
import com.example.comitserver.entity.CreatedStudyEntity;
import com.example.comitserver.entity.EventEntity;
import com.example.comitserver.entity.StudyEntity;
import com.example.comitserver.entity.UserEntity;
import com.example.comitserver.repository.CreatedStudyRepository;
import com.example.comitserver.repository.EventRepository;
import com.example.comitserver.repository.StudyRepository;
import com.example.comitserver.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventEntity> showAllEvents() {
        return eventRepository.findAll();
    }

    public EventEntity showEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));
    }

    public EventEntity createEvent(EventRequestDTO eventRequestDTO) {
        // Create a new study and assign the current user as mentor
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

    //

    private void fillEventFields(EventEntity event, EventRequestDTO dto) {
        // Fill in all study fields except for mentor
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
