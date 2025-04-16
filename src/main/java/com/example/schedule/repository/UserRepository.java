package com.example.schedule.repository;

import com.example.schedule.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email); // 해당 이메일이 존재하는지 체크
    //Long id(Long id); 이건 삭제
    Optional<User> findByEmail(String email); //이메일 기반 로그인이라서 로그인 검증에 쓰기 위해 작성
}
