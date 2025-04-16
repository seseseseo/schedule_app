package com.example.schedule.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id") //외래키 명시
    private Long id;

    // X To One 은 기본이 즉시로딩이라 꼭 Lazy를 해줘야함 N+1 문제 방지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") //외래키 명시
    private User user; // 단방향, 외래키가 있는 곳이 연관관계의 주인

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;
    private LocalDateTime updateAt;
    // 생성자 (비공개 생성) 외부에서 new를 사용해서 생성하지 못하게 private로 설정
    private Schedule(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }
    //생성용 정적 팩토리 메서드를 이용한 객체 생성, new사용하기 싫어서
    public static Schedule of(User user, String title, String content) {
        return new Schedule(user, title, content);
    }
    //setter 대신 행위로 표현
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updateAt = LocalDateTime.now(); // auditing을 사용하고 있을텐데.. 이부분은 수정하는게 맞아보인다
    }

    // 양방향 연관관계는 안티패턴이라 지향해야되는데 이걸 할 필요가 있나에 대한 고민 (맥락에 따라 다르겠지만 이 상황에선 어찌해야할지 모르겠음)
    // 스케줄 1 : 댓글 N
    // 양방향 매핑에선 mappedBy는 주인이 아님을 명시
    // cascade를 사용해서 일정삭제 시 댓글도 자동 삭제
    // orphanRemoval 댓글이 더이상 소속된 일정이 없으면..(부모가 없으면)? DB에서도 삭제됨
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // 댓글을 조회할 때, 댓글이 소속된 일정을 참조해야 할까? O
    // 일정을 조회할 때 그 일정의 댓글들을 항상 보여줘야하나 O


    // 그럼 연관관계 편의 메서드를 어떻게 정의해야하는가?
     public void addComment(Comment comment) {
         comments.add(comment);
         comment.setSchedule(this);
     }
     public void removeComment(Comment comment) {
         comments.remove(comment);
         comment.setSchedule(null);
     }
}
