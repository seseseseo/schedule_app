package com.example.schedule.domain;

import com.example.schedule.domain.entity.Schedule;
import com.example.schedule.domain.entity.User;
import com.example.schedule.repository.ScheduleRepository;
import com.example.schedule.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Rollback(value = false)
@Transactional
class UserEntityTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ScheduleRepository scheduleRepository;
    @PersistenceContext
    EntityManager em;

    //crud 구현 전, jpa 엔티티 단위 테스트 하고 싶어서 EntityManager를 활용해 테스트를 해봄
    @Test
    public void testEntity() {
        User user1 = User.of("admin", "admsadadasin", "admsain@gmail.com");
        em.persist(user1);

        Schedule schedule1 = Schedule.of(user1, "회의", "회의를 합니다" );
        em.persist(schedule1);

        em.flush();
        em.clear(); //1차 캐시 제거,

        Schedule sc = em.find(Schedule.class, schedule1.getId());
        String username = user1.getUsername();
        assertThat(username).isEqualTo(user1.getUsername());
    }

}