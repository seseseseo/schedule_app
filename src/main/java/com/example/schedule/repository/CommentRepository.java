package com.example.schedule.repository;

import com.example.schedule.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.schedule.domain.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByScheduleId(Long scheduleId);
    // FindBy ; SQL 문에서 where 절 역할
}
