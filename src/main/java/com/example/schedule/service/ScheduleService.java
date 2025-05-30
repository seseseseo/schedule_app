package com.example.schedule.service;

import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ScheduleService {
    //일정 등록
    ScheduleResponseDto saveWithUser(ScheduleRequestDto dto, Long userId);

    // 단건 조회
    ScheduleResponseDto findById(Long id);

    // 일정 삭제
    void deleteById(Long id);

    //단건 조회에서 비밀번호 체크
    void checkPassword(Long scheduleId, String password);

    void updateSchedule(Long id, @Valid ScheduleRequestDto requestDto, String password);

    void deleteScheduleWithPassword(Long id, String password);

    Page<ScheduleResponseDto> findAll(Pageable pageable);
}
