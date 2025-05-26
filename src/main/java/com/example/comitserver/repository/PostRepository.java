package com.example.comitserver.repository;

import com.example.comitserver.entity.PostEntity;
import com.example.comitserver.entity.enumeration.GroupType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByGroupIdAndGroupType(Long groupId, GroupType groupType);
}
