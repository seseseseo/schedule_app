package com.example.schedule.controller;

import com.example.schedule.common.SessionConst;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
        // 요청 바디의 json -> dto로 변환되고 httpserveletrequest는 로그인한 유저 정보를 꺼내기 위해 사용됨
        UserResponseDto loginUser = (UserResponseDto) request.getAttribute(SessionConst.LOGIN_USER);
        //필터에서 설정한거 꺼내옴
        if (loginUser == null) { //로그인이 안된 경우 예외 발생
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        Long userId = loginUser.getId(); //로그인된 사용자의 id를 꺼냄
        ScheduleResponseDto responseDto = scheduleService.saveWithUser(requestDto, userId);
        //login userId를 같이 넘겨서 일정 작성자를 설정, 서비스 계층을 통해 일정 저장
        return ResponseEntity // 응답은 htp 201 created
                .created(URI.create("/api/schedules/" + responseDto.getId()))
                // 이부분도 url가 직접 들어가있는데 그럼 하드코딩인가? 어떻게 수정해야하는지 모르겠음.
                .body(responseDto);
    }
    //일정 전체 조회 + 이부분 없어서 추가함 Pageable을 이용함
    @GetMapping
    public ResponseEntity<Page<ScheduleResponseDto>> getAllSchedules(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
            ) {
        Page<ScheduleResponseDto> schedules = scheduleService.findAll(pageable);
        return ResponseEntity.ok(schedules);
    }

    // 일정 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> findById(@PathVariable Long id) {
        //url 경로에서 id를 뽑아와서 조회
        ScheduleResponseDto responseDto = scheduleService.findById(id);
        return ResponseEntity.ok(responseDto); //
    }

    // 일정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
