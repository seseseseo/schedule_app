package com.example.schedule.service;

import com.example.schedule.domain.entity.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    Schedule save(Schedule schedule);
    List<Schedule> findAll();
    Optional<Schedule> findById(Long id);
    void deleteById(Long id);
}
