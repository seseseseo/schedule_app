package com.example.schedule.repository;

import com.example.schedule.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s JOIN FETCH s.user WHERE s.id = :id")
    Optional<Schedule> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT s FROM Schedule s JOIN FETCH s.user")
    List<Schedule> findAllWithUser();

}
