package com.example.schedule.controller;

import com.example.schedule.domain.entity.Schedule;
import com.example.schedule.domain.entity.User;
import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.exception.CustomException;
import com.example.schedule.exception.ErrorCode;
import com.example.schedule.service.ScheduleService;
import com.example.schedule.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final UserService userService;

    //일정 등록
    @PostMapping
    public ResponseEntity<ScheduleResponseDto> save(@RequestBody ScheduleRequestDto requestDto,
                                                    HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션 가져오기
        if (session == null || session.getAttribute("userId") == null) {
            // 세션이 없거나 userId가 없으면 에러코드
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        // 이후에 . 값을 꺼내서 로그인한 유저 정보를 가져와서 일정 등록함
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 유저 객체와 요청 정보로 일정 생성
        Schedule schedule = Schedule.of(user,requestDto.getTitle(), requestDto.getContent());
        // 디비에 저장
        Schedule saved = scheduleService.save(schedule);
        return ResponseEntity.created(URI.create("/api/schedules/" + saved.getId()))
                .body(new ScheduleResponseDto(saved));
    }
    // 일정 전체 조회
    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto>> findAll() {
        List<ScheduleResponseDto> result = scheduleService.findAll().stream()
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // 일정 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> findById(@PathVariable Long id) {
        Optional<Schedule> schedule = scheduleService.findById(id);
        return ResponseEntity.ok(new ScheduleResponseDto(schedule.orElse(null)));
    }

    // 일정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
