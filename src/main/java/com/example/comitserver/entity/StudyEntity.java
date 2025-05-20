package com.example.comitserver.entity;

import com.example.comitserver.entity.enumeration.Campus;
import com.example.comitserver.entity.enumeration.Day;
import com.example.comitserver.entity.enumeration.Level;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity // 실제 entity임을 명시
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StudyEntity extends GroupBaseEntity{

    @ManyToOne(fetch = FetchType.EAGER) // FetchType이 LAZY면 문제가 발생
    @JoinColumn(name = "user_id")
    private UserEntity mentor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Day day;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Campus campus;
}
