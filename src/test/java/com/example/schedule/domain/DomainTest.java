package com.example.schedule.domain;

import com.example.schedule.domain.entity.Schedule;
import com.example.schedule.domain.entity.User;
import com.example.schedule.repository.ScheduleRepository;
import com.example.schedule.repository.UserRepository;
import com.example.schedule.service.ScheduleService;
import com.example.schedule.service.ScheduleServiceImpl;
import com.example.schedule.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@Rollback(false)
public class DomainTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ScheduleRepository scheduleRepository;

    @Test
    public void testUserSave() {
        User user = User.of("길동이","asdafasd","hong@naver.com");
        User saveUser = userRepository.save(user);

        Schedule schedule = Schedule.of(user, "할일","내용입니다");
        Schedule saveSchedule = scheduleRepository.save(schedule);

        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(saveUser.getId()).isNotNull();
        assertThat(saveSchedule.getId()).isNotNull();
        assertThat(saveUser.getUsername()).isEqualTo(saveSchedule.getUser().getUsername());
    }
}
