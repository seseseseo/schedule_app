package com.example.schedule.repository;

import com.example.schedule.domain.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s JOIN FETCH s.user WHERE s.id = :id")
    Optional<Schedule> findByIdWithUser(@Param("id") Long id);

    @Query(value = "SELECT s FROM Schedule s JOIN FETCH s.user",
    countQuery = "SELECT COUNT(s) FROM Schedule s")
    Page<Schedule> findAllWithUser(Pageable pageable);



}
