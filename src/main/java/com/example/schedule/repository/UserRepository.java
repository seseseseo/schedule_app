package com.example.schedule.repository;

import com.example.schedule.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Long id(Long id);
    Optional<User> findByEmail(String email);
}
