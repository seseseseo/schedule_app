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

    @NotBlank(message = "제목은 필수 입력값입니다.") // null, "", " "  전부 안됨
    @Size(max = 10, message = "제목은 10자 이내여야 합니다.") // 길이제한
    public String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    public String content;

    private String username;
    private LocalDateTime createdAt;

    // 엔티티를 받아서 DTO로 변환하는 생성자
    //Schedule 객체에서 필요한 필드만 추출
    public ScheduleRequestDto(Schedule schedule) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.content = schedule.getContent();
        this.username = schedule.getUser().getUsername();
        this.createdAt = schedule.getCreatedDate();
    }
}
