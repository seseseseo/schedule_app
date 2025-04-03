package com.example.schedule.dto;

import com.example.schedule.domain.entity.Schedule;
import lombok.Getter;

@Getter
public class ScheduleResponseDto {
    private final Long id;
    private final String username;
    private final String title;
    private final String content;

    public ScheduleResponseDto(Schedule schedule) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.content = schedule.getContent();
        this.username = schedule.getUser().getUsername();
    }
}
