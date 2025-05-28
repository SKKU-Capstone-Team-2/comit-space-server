package com.example.comitserver.entity;

import com.example.comitserver.entity.enumeration.JoinState;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Data
@Entity
// study & user 관계를 기록하는 테이블을 위한 것임
// study 생성 기록 추적 또는 내가 만든 스터디만 따로 조회할 때 유용함
public class CreatedStudyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "study_id", nullable = false)
    private StudyEntity study;

    @Getter
    @Column(nullable = false)
    private boolean isLeader;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JoinState state;

}
