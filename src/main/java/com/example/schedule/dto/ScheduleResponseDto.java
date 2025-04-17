package com.example.schedule.dto;

import com.example.schedule.domain.entity.Schedule;
import com.example.schedule.domain.entity.User;
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
    //response Dto에서 final 키워드
    // 서버에서 클라이언트로 출력되는 데이터 전달 객체
    // final을 사용해 불변 객체로 만들었음

    public ScheduleResponseDto(Schedule schedule) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.content = schedule.getContent();
        this.username = schedule.getUser().getUsername();
        this.createdAt = schedule.getCreatedDate();
        this.userId = schedule.getUser().getId();
    }


    // 정적 팩토리 메서드 사용
    public static ScheduleResponseDto from(Schedule schedule) {
        return new ScheduleResponseDto(schedule); // 재사용
    }
}
