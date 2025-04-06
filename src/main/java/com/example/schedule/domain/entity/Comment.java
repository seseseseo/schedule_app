package com.example.schedule.domain.entity;

import com.example.schedule.dto.CommentDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
public class Comment extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String writer;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    protected Comment() {}


    public Comment(String content, String writer, Schedule schedule) {
        this.content = content;
        this.writer = writer;
        this.schedule = schedule;
    }
    public CommentDto toDto() {
        CommentDto dto = new CommentDto();
        dto.setId(this.id);
        dto.setContent(this.content);
        dto.setWriter(this.writer);
        dto.setCreatedAt(this.getCreatedDate()); //
        dto.setScheduleId(this.schedule.getId());
        return dto;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
