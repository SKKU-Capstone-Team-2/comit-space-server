package com.example.comitserver.entity;

import com.example.comitserver.entity.enumeration.Semester;
import com.example.comitserver.utils.StringListConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@MappedSuperclass // 상속받는 클래스가 필드들을 칼럼으로 인식하게 함
@Data // getter, setter 등 자동 생성
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 생성
@SuperBuilder
public abstract class GroupBaseEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false)
    protected String title;

    @Column(nullable = false)
    protected String imageSrc;

    @Column(nullable = false)
    private String startTime;

    @Column(nullable = false)
    private String endTime;

    @Column(nullable = false)
    protected String description;

    @Column(nullable = false)
    protected Boolean isRecruiting;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected Semester semester;

    @Column(nullable = false)
    protected Integer year;

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(nullable = true)
    protected List<String> tags = new ArrayList<>();

    // setter 커스터마이징
    // NPE 대비
    public void setTags(List<String> tags) {
        this.tags = (tags != null) ? tags : new ArrayList<>();
    }
}