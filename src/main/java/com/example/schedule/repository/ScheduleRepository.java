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
    //스케줄을 조회할 때 연관된 User까지 같이 조회하고 싶어서 사용
    // 하지만 ManyToOne은 fetch=Lazy라서(지연로딩 방식은 나중에 꺼내오는 것) 그래서 유저를 호출할 때 n+1문제가 발생할 수 있다
    // 그래서 join fetch를 사용해 연관된 객체도 같이 가져와라고 시킴
    // 이 쿼리 1번으로 Schedule+user를 모두 가져온다
    // 지연로딩은 가볍지만 필요할 때마다 쿼리를 추가적으로 날리기 때문에 이것을 막기 위해 join fetch를 사용한다가 맞을까?


    @Query(value = "SELECT s FROM Schedule s JOIN FETCH s.user",
    countQuery = "SELECT COUNT(s) FROM Schedule s")
    Page<Schedule> findAllWithUser(Pageable pageable);
//일정 전체 조회 시 작성자 정보를 한꺼번에 조회해야하는데
    // 페이징을 적용하기 위해 countQuery를 사용했다.
    // join fetch는 페이징과 같이 사용하면 문제가 될 수 있다길래
    // (Pageable은 전체 개수를 구해야 하는데, fetch join은 중복된 row가 생겨 count가 틀어질 수 있음
    // 그래서 countQuery를 따로 명시해서 올바른 페이지 수를 계산


    // Spring Data Jpa에서는 이름 기반으로 쿼리를 자동으로 생성할 수 있지만
    // 연관된 엔티티 유저도 가져와야하고
    // 페이징과 Fetch Join을 사용하기 위해서
    // 이름 기반 쿼리를 사용하는 방식은 안좋을 듯 하여 JPQL을 사용했다.

}
