package com.example.schedule.controller;

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
    public ResponseEntity<?> register(@Valid
                                      @RequestBody UserRequestDto userRequestDto,
                                      BindingResult bindingResult,
                                      HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        // 유효성 검사 성공 시, 회원가입 처리
        Long userId = userService.register(userRequestDto);
        // 회원가입 후 사용자 정보 조회
        UserResponseDto responseDto = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
        UserResponseDto userId = userService.login(user.getEmail(), user.getPassword())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PASSWORD));
        request.getSession(true).setAttribute("userId", userId);
        return ResponseEntity.ok("로그인 성공");
    }

    // 로그아웃 : 세션제거
    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("로그아웃 완료");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
