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
@RequiredArgsConstructor
public class CommentService {
    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;

    public List<CommentDto> getCommentByScheduleId(Long ScheduleId) {
        List<Comment> comments = commentRepository.findByScheduleId(ScheduleId);
        List<CommentDto> result = new ArrayList<>();
        for (Comment comment : comments) {
            result.add(comment.toDto());
        }
        return result;
    }
    @Transactional
    public void addComment(CommentDto dto) {
        Schedule schedule = scheduleRepository.getReferenceById(dto.getScheduleId());

        Comment comment = new Comment(dto.getContent(), dto.getWriter(), schedule);
        commentRepository.save(comment);
    }
    @Transactional
    public void deleteComment(Long commmentId) {
        commentRepository.deleteById(commmentId);
    }
    public CommentDto findById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다: " + commentId));
        return comment.toDto();
    }

    @Transactional
    public void updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다: " + commentId));

        comment.updateContent(content);
    }
}
