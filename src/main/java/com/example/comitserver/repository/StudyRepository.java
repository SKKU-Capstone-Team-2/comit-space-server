package com.example.comitserver.repository;

import com.example.comitserver.entity.StudyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// spring data JPA에서 자동으로 CRUD 메서드 만들어줌
public interface StudyRepository extends JpaRepository<StudyEntity, Long> {}
