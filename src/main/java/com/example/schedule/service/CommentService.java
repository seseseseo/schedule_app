package com.example.schedule.service;

import com.example.schedule.domain.entity.Comment;
import com.example.schedule.domain.entity.Schedule;
import com.example.schedule.dto.CommentDto;
import com.example.schedule.repository.CommentRepository;
import com.example.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor //final 필드에 대한 의존성 주입
public class CommentService {
    private final ScheduleRepository scheduleRepository; // 댓글은 스케줄에 종속됨으로 repository
    private final CommentRepository commentRepository;

    public List<CommentDto> getCommentByScheduleId(Long ScheduleId) {
        List<Comment> comments = commentRepository.findByScheduleId(ScheduleId); // 특정 스케줄 아이디에 속한 댓글들을 찾아서
        List<CommentDto> result = new ArrayList<>(); // DTO로 반환한다
        for (Comment comment : comments) {
            result.add(CommentDto.from(comment));
        }
        return result;
    }
    @Transactional
    public void addComment(CommentDto dto) {
        Schedule schedule = scheduleRepository.getReferenceById(dto.getScheduleId());
        // getReferenceId를 쓴 이유
        // DB에서 즉시 조회하지않고 해당 엔티티에 대한 프록시(까짜)만 먼저 반환한다
        // 실제로 접근할 때 그때 쿼리를 날린다(약간 지연로딩처럼 동작)
        // 댓글을 조회할 때에는 전체 일정 정보를 알 필요가 없고 schedule_id만 알면 되니까
        // 이것을 사용했다.
        Comment comment = Comment.create(dto.getContent(), dto.getWriter(), schedule); // 정적 팩터리 메서드를 ㅅ사용했음
        //이때 쿼리가 실행됨
        commentRepository.save(comment);
    }
    @Transactional
    public void deleteComment(Long commmentId) {
        commentRepository.deleteById(commmentId);
    }
    public CommentDto findById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다: " + commentId));
        return CommentDto.from(comment);
    }
    // 댓글의 내용만 수정
    @Transactional
    public void updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다: " + commentId));

        comment.updateContent(content);
    }
}
