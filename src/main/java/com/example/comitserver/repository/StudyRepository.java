package com.example.comitserver.repository;

import com.example.comitserver.entity.StudyEntity;
import com.example.comitserver.entity.UserEntity;
import com.example.comitserver.entity.enumeration.Day;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

// spring data JPA에서 자동으로 CRUD 메서드 만들어줌
public interface StudyRepository extends JpaRepository<StudyEntity, Long> {
    boolean existsByMentorAndDayAndStartTimeAndEndTime(
            UserEntity mentor,
            Day day,
            String startTime,
            String endTime
    );

}
