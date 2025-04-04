package com.example.schedule.dto;

import com.example.schedule.domain.entity.Schedule;
import com.example.schedule.domain.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequestDto {
    private Long id;

    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(max = 10, message = "제목은 10자 이내여야 합니다.")
    public String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    public String content;

    private String username;
    private LocalDateTime createdAt;

    public ScheduleRequestDto(Schedule schedule) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.content = schedule.getContent();
        this.username = schedule.getUser().getUsername();
        this.createdAt = schedule.getCreatedDate();
    }
}
