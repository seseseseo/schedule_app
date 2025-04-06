package com.example.schedule.controller;

import com.example.schedule.domain.entity.User;
import com.example.schedule.dto.CommentDto;
import com.example.schedule.dto.UserResponseDto;
import com.example.schedule.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/schedule/{scheduleId}/comments")
    public String createComment(@PathVariable Long scheduleId,
                                @RequestParam String content,
                                HttpServletRequest request) {
        UserResponseDto userDto = (UserResponseDto) request.getAttribute("loginUser");
        String writer = (userDto != null) ? userDto.getUsername() : "비회원";

        CommentDto dto = new CommentDto();
        dto.setScheduleId(scheduleId);
        dto.setContent(content);
        dto.setWriter(writer);

        commentService.addComment(dto);
        return "redirect:/schedule/" + scheduleId;
    }
    @GetMapping("/schedule/{scheduleId}/comments")
    public String showComments(@PathVariable Long scheduleId, Model model) {
        List<CommentDto> comments = commentService.getCommentByScheduleId(scheduleId);
        model.addAttribute("comments", comments);
        return "schedule/comments";
    }
    @PostMapping("/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId,
                                @RequestParam Long scheduleId) {
        commentService.deleteComment(commentId);
        return "redirect:/schedule/" + scheduleId;
    }
}
