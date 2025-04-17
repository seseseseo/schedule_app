package com.example.schedule.repository;

import com.example.schedule.domain.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
// JpaRepository<Schedule, Long>을 상속함으로써 기본적인 CRUD (findById, save, delete) 기능 제공 받고
// 추가적으로 커스텀 쿼리를 정의하기 위해

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // 쿼리 어노테이션 : JPQL 쿼리를 직접 작성해서 실행
    @Query("SELECT s FROM Schedule s JOIN FETCH s.user WHERE s.id = :id")
    Optional<Schedule> findByIdWithUser(@Param("id") Long id);//JPQL에서 :id로 바인딩한 값을 실제 메서드 매개변수 id에 연결해주는 어노테이션
    //스케줄을 조회할 때 연관된 User까지 같이 조회하고 싶어서 사용
    // 하지만 ManyToOne은 fetch=Lazy라서(지연로딩 방식은 나중에 꺼내오는 것)
    // 그래서 유저를 호출할 schedule.getUser() 때 n+1문제가 발생할 수 있다
    // 그래서 join fetch를 사용해 연관된 객체도 같이 가져와라고 시킴
    // join fetch는 지연로딩 대신 즉시로딩처럼 동작하게 만들어서
    // 이 쿼리 1번으로 연관된 Schedule+user를 한 번의 쿼리로 가져온다
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

    // 여기서 궁금했던 점이 하나 있었습니다. 여기서는 즉시 로딩처럼 동작하게 사용했는데
    // 그럼 ManyToOne(fetch= FetchType.LAZY)를 기본으로 두는 이유
    // 이유1. 불필요한 데이터 로딩 방지 : 대부분 스케줄만 필요하지 유저까지 항상 필요한게 아님
    // EAGER로 설정하면 스케줄을 불러올 때마다 무조건 유저 쿼리도 실행돼서 성능저하
    // 이유2. N + 1 문제 원인 제공 : 즉시 로딩 설정은 리스트 조회 시 무조건 추가 쿼리가 발생한다.
    // 따라서 필요한 경우에만 fetch join으로 즉시로딩한다
}
