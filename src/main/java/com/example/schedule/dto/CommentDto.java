package com.example.schedule.dto;

import com.example.schedule.domain.entity.Comment;
import com.example.schedule.domain.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private String writer;
    private LocalDateTime createdAt;
    private Long scheduleId;


    // 정적 팩토리 메서드 패턴을 사용해서 객체 생성을 한 이유

    // CommentDto.from()이렇게 정적 메서드로 객체를 생성하면 의미있는 이름을 부여할 수 있고
    // new보다 가독성이 향상되고
    // 일단 이러한 이유로 정적 팩토리 패턴을 사용했음
    public static CommentDto from(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getWriter(),
                comment.getCreatedDate(),
                comment.getSchedule().getId()
        );
    }

}
