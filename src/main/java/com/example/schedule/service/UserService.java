package com.example.schedule.service;

import com.example.schedule.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(User user); //회원가입
    List<User> findAllUsers(); //회원 전체 보기
    Optional<User> findById(Long id); //유저 단건 조회
    Optional<User> login(String email, String password);
    void deleteUser(Long id);

}
