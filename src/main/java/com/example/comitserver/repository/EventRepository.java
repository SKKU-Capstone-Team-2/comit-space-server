package com.example.comitserver.repository;

import com.example.comitserver.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// spring data JPA에서 자동으로 CRUD 메서드 만들어줌
public interface EventRepository extends JpaRepository<EventEntity, Long> {}
