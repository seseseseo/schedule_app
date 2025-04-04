package com.example.schedule.dto;

import com.example.schedule.domain.entity.Schedule;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleResponseDto {
    private final Long id;
    private Long userId;
    private final String username;
    private final String title;
    private final String content;
    private LocalDateTime createdAt;
    public ScheduleResponseDto(Schedule schedule) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.content = schedule.getContent();
        this.username = schedule.getUser().getUsername();
        this.createdAt = schedule.getCreatedDate();
        this.userId = schedule.getUser().getId();
    }
}
