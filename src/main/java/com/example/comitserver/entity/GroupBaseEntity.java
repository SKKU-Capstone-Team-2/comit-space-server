package com.example.comitserver.entity;

import com.example.comitserver.entity.enumeration.Semester;
import com.example.comitserver.utils.StringListConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
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

    public void setTags(List<String> tags) {
        this.tags = (tags != null) ? tags : new ArrayList<>();
    }
}