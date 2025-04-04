package com.example.schedule.controller;

import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.dto.UserResponseDto;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ApiScheduleController {
    private final ScheduleService scheduleService;
    private final UserService userService;

    //일정 등록
    @PostMapping
    public ResponseEntity<ScheduleResponseDto> save(@RequestBody ScheduleRequestDto requestDto,
                                                    HttpServletRequest request) {
        UserResponseDto loginUser = (UserResponseDto) request.getAttribute("loginUser");
        if (loginUser == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        Long userId = loginUser.getId();
        ScheduleResponseDto responseDto = scheduleService.saveWithUser(requestDto, userId);
        return ResponseEntity
                .created(URI.create("/api/schedules/" + responseDto.getId()))
                .body(responseDto);
    }
    // 일정 전체 조회
//    @GetMapping
//    public ResponseEntity<List<ScheduleResponseDto>> findAll() {
//        return ResponseEntity.ok(scheduleService.findAll());
//    }

    // 일정 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> findById(@PathVariable Long id) {
        ScheduleResponseDto responseDto = scheduleService.findById(id);
        return ResponseEntity.ok(responseDto);
    }

    // 일정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
