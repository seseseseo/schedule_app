package com.example.schedule.controller;

import com.example.schedule.domain.entity.User;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ScheduleService scheduleService;

    //회원가입 UserRequestDto -> User저장 -> id반환 -> UserResponseDto생성
    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDto userRequestDto,
                                                    BindingResult bindingResult,
                                                    HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            // 오류 메시지를 List<String> 형태로 반환
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }
        // 유효성 검사 성공 시, 회원가입 처리
        Long userId = userService.register(userRequestDto);
        // 회원가입 후 사용자 정보 조회
        UserResponseDto responseDto = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // 정상적인 응답: 201 Created와 함께 생성된 사용자 정보를 반환
        return ResponseEntity.created(URI.create("/api/users/" + responseDto.getId()))
                .body(responseDto);
    }
    //유저 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        UserResponseDto responseDto = userService.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok(responseDto);
    }
    //유저 전체 조회
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    // 로그인 처리 :email + pw -> userId -> 세션 저장
    @PostMapping("/{id}")
    public ResponseEntity<String> login(@Valid @RequestBody UserRequestDto userRequestDto,
                                   HttpServletRequest request) {
        Long userId = userService.login(userRequestDto.getEmail(), userRequestDto.getPassword())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PASSWORD));
        // Session이 있으면 가져오고 없으면 Session을 생성해서 return (default = true)
        HttpSession session = request.getSession(true);
        session.setAttribute("userId",userId); // 사용자가 로그인 할 때 이 값을 세션에 저장해둠
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
