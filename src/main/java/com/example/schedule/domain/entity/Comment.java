package com.example.schedule.domain.entity;

import com.example.schedule.dto.CommentDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter // 엔티티에서의 무분별한 수정을 막기 위해 세터나 @Data 어노테이션 사용을 지양했음
public class Comment extends BaseTime{//공통 필드를 상속받게 함
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String writer;

    //N:1 연관관계 여러 Comment가 스케줄 하나에 연결됨
    @ManyToOne(fetch = FetchType.LAZY) //Lazy설정을 둬서 N+1 문제를 방지, 하지만 아직까지 이거에 대해 잘 모름
    @JoinColumn(name = "schedule_id") // 와래키를 명시함
    private Schedule schedule;

    protected Comment() {}


    public Comment(String content, String writer, Schedule schedule) {
        this.content = content;
        this.writer = writer;
        this.schedule = schedule;
    }

    //기존의 코드 entity에서 dto로 변환하는 코드가 있어서 comment dto쪽으로 분리했음. 책임을 분리한게 맞나?

    public static Comment create(String content, String writer, Schedule schedule) {
        return new Comment(content, writer, schedule);
    }
    public void updateContent(String content) {
        this.content = content;
    }

    // 연관관계 편의를 위한 메서드
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
