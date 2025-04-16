package com.example.schedule.controller;

import com.example.schedule.common.SessionConst;
import com.example.schedule.dto.UserRequestDto;
import com.example.schedule.dto.UserResponseDto;

import com.example.schedule.exception.CustomException;
import com.example.schedule.exception.ErrorCode;
import com.example.schedule.service.ScheduleService;
import com.example.schedule.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class ApiUserController {
    private final UserService userService;

    //회원가입 UserRequestDto -> User저장 -> id반환 -> UserResponseDto생성
    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDto userRequestDto,
                                      BindingResult bindingResult,
                                      HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            //검증 실패 시, 에러메세지 전부 반환
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        // 유효성 검사 성공 시, 회원가입 처리, 서비스를 통해 DB에 저장
        Long userId = userService.register(userRequestDto);
        // 회원가입 후 사용자 정보 조회하고 응답 DTO로 반환
        UserResponseDto responseDto = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // 201 create 반환
        return ResponseEntity.created(URI.create("/api/users/" + responseDto.getId()))
                .body(responseDto);
    }
    //유저 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                userService.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
        );
    }
    //유저 전체 조회
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    // 로그인 처리 :email + pw -> userId -> 세션 저장
    @PostMapping("/{id}")
    public ResponseEntity<String> login(@Valid @RequestBody UserRequestDto user,
                                   HttpServletRequest request) {
        //로그인 시 이미 세션이 있는지 중복 방지 및 기존 코드 로그인 체크 기능을 추가로 넣었음
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SessionConst.LOGIN_USER) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 로그인된 사용자입니다");
        }
        UserResponseDto loginUser = userService.login(user.getEmail(), user.getPassword())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PASSWORD));

        //session 생성 및 유저 정보 저장
        request.getSession(true).setAttribute(SessionConst.LOGIN_USER, loginUser);
        return ResponseEntity.ok("로그인 성공");
    }

    // 로그아웃 : 세션제거
    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션이 없으면 false
        if (session != null) {
            session.invalidate(); // 세션 제거
        }
        return ResponseEntity.ok("로그아웃 완료");
    }

    //회원 탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204 반환
    }

}
