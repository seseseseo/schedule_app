package com.example.schedule.repository;

import com.example.schedule.domain.entity.Schedule;
import com.example.schedule.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class ScheduleRepositoryTest {
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    private UserRepository userRepository;


}