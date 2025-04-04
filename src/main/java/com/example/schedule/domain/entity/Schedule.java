package com.example.schedule.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    // X To One 은 기본이 즉시로딩이라 꼭 Lazy를 해줘야함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 단방향, 외래키가 있는 곳이 연관관계의 주인

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;
    private LocalDateTime updateAt;
    // 생성자 (비공개 생성)
    private Schedule(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }
    //정적 팩토리
    public static Schedule of(User user, String title, String content) {
        return new Schedule(user, title, content);
    }
//
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updateAt = LocalDateTime.now();
    }
}
