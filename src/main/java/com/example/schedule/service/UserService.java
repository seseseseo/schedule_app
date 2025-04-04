package com.example.schedule.service;

import com.example.schedule.dto.UserRequestDto;
import com.example.schedule.dto.UserResponseDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // 회원가입 : 회원 등록 후 userId 반환
    Long register(UserRequestDto dto);
    //회원 전체 보기 (목록으로 볼 수 있게)
    List<UserResponseDto> findAllUsers();
    //유저 단건 조회
    Optional<UserResponseDto> findById(Long id);
    // 로그인 성공 시 userId 반환 (없으면 Optional.empty)
    Optional<Long> login(String email, String password);
    //유저 업데이트
    void updateUserWithPasswordCheck(Long id, UserRequestDto dto, String password);
    //유저 삭제
    void deleteUser(Long id);
    //비밀번호 체크


}
