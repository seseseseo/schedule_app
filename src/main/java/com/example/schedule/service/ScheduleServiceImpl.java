package com.example.schedule.service;

import com.example.schedule.config.PasswordEncoder;
import com.example.schedule.domain.entity.Schedule;
import com.example.schedule.domain.entity.User;
import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.exception.CustomException;
import com.example.schedule.exception.ErrorCode;
import com.example.schedule.repository.ScheduleRepository;
import com.example.schedule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
// 이 클래스가 서비스 계층의 컴포넌트임을 나타내는 어노테이션
// Spring 이 이 클래스를 빈으로 등록해 관리하게 해줌
@Transactional
// 하나의 트랜잭션으로 묶여서 실행되도록 해주
//  DB 작업이 여러 번 수행되더라도 모두 성공하면 커밋, 하나라도 실패하면 롤백
// 댓글은 1. 스케줄 프록시 조회 2. 댓글 저장 두가지 작업을 포함해서 하나라도 실패하면 전체 작업을 롤백하는게 맞다고 생각함
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService{

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 비교에 사용

    /**
     * 일정 저장 (DTO + 사용자 ID 기반)
     */
    @Override
    public ScheduleResponseDto saveWithUser(ScheduleRequestDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // 사용자를 찾아서
        Schedule schedule = Schedule.of(user, dto.getTitle(), dto.getContent());
        // 정적팩토리 메서드를 이용해 일정을 생성하고
        Schedule saved = scheduleRepository.save(schedule);
        // 저장했다
        return new ScheduleResponseDto(saved);
    }

    @Override
    public ScheduleResponseDto findById(Long id) {
        Schedule schedule = scheduleRepository.findByIdWithUser(id)
                //fetch join으로 유저까지 함께 조회 후ㅜ 없으면 커스텀 에러 던짐
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        return new ScheduleResponseDto(schedule);
    }

    @Override
    public void deleteById(Long id) {
        // 삭제 전에 존재 여부 체크하고 삭제
        if (!scheduleRepository.existsById(id)) {
            throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
        }
        scheduleRepository.deleteById(id);
    }

    @Override
    public void checkPassword(Long scheduleId, String password) {
        Schedule schedule = scheduleRepository.findByIdWithUser(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        User user = schedule.getUser();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
//        if (!password.matches(user.getPassword())) {
//            throw new CustomException(ErrorCode.INVALID_PASSWORD);
//        }
    }

    @Override
    public void updateSchedule(Long id, ScheduleRequestDto requestDto, String password) {
        Schedule schedule = scheduleRepository.findByIdWithUser(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        //업데이트 . 사용자 비밀번호 검증
        if (!passwordEncoder.matches(password, schedule.getUser().getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        schedule.update(requestDto.getTitle(), requestDto.getContent());
    }

    @Override
    public void deleteScheduleWithPassword(Long id, String password) {
        Schedule schedule = scheduleRepository.findByIdWithUser(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!passwordEncoder.matches(password, schedule.getUser().getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        scheduleRepository.delete(schedule);
    }

    @Override
    public Page<ScheduleResponseDto> findAll(Pageable pageable) {
        //페이징 처리
        Page<Schedule> page = scheduleRepository.findAllWithUser(pageable);
        return page.map(ScheduleResponseDto::from);
        //map의 사용이 좋을까?
    }
}
