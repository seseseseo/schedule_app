package com.example.schedule.service;

import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;
import jakarta.validation.Valid;

import java.util.List;

public interface ScheduleService {
    //일정 등록
    ScheduleResponseDto saveWithUser(ScheduleRequestDto dto, Long userId);

    // 전체 일정 조회
    List<ScheduleResponseDto> findAll();

    // 단건 조회
    ScheduleResponseDto findById(Long id);

    // 일정 삭제
    void deleteById(Long id);

    //단건 조회에서 비밀번호 체크
    void checkPassword(Long scheduleId, String password);

    void updateSchedule(Long id, @Valid ScheduleRequestDto requestDto, String password);

    void deleteScheduleWithPassword(Long id, String password);
}
