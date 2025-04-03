package com.example.schedule.controller;

import com.example.schedule.domain.entity.User;
import com.example.schedule.dto.UserRequestDto;
import com.example.schedule.dto.UserResponseDto;

import com.example.schedule.exception.CustomException;
import com.example.schedule.exception.ErrorCode;
import com.example.schedule.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    //회원가입
    @PostMapping
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto userRequestDto) {
        User user = User.of(userRequestDto.getUsername(), userRequestDto.getPassword(), userRequestDto.getEmail());
        User saveUser = userService.register(user);
        return ResponseEntity.created(URI.create("/api/users/" + saveUser.getId()))
                .body(new UserResponseDto(saveUser));
    }
    //유저 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok(new UserResponseDto(user));
    }
    //유저 전체 조회
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        List<UserResponseDto> result = userService.findAllUsers()
                .stream().map(UserResponseDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> login(@RequestBody UserRequestDto userRequestDto,
                                   HttpServletRequest request) {
        User user = userService.login(userRequestDto.getEmail(), userRequestDto.getPassword())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PASSWORD));
        // Session이 있으면 가져오고 없으면 Session을 생성해서 return (default = true)
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId()); // 사용자가 로그인 할 때 이 값을 세션에 저장해둠
        return ResponseEntity.ok("로그인 성공");
    }

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
