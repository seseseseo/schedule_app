package com.example.schedule.controller;

import com.example.schedule.config.PasswordEncoder;
import com.example.schedule.domain.entity.Schedule;
import com.example.schedule.dto.*;
import com.example.schedule.exception.CustomException;
import com.example.schedule.exception.ErrorCode;
import com.example.schedule.repository.ScheduleRepository;
import com.example.schedule.service.ScheduleService;
import com.example.schedule.service.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
//홈 화면 로그인 여부에 따른 뷰 보이기
public class HomeController {
    private final UserService userService;
    private final ScheduleService scheduleService;
    private final ScheduleRepository scheduleRepository;
    private final PasswordEncoder passwordEncoder;

    public HomeController(UserService userService, ScheduleService scheduleService, ScheduleRepository scheduleRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.scheduleService = scheduleService;
        this.scheduleRepository = scheduleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        log.info("home controller");
        UserResponseDto loginUser = (UserResponseDto) request.getAttribute("loginUser");
        if (loginUser != null) {
            model.addAttribute("loginUser", loginUser);
        }
        return "home";
    }


    @PostMapping("/schedule/{id}/check-password")
    public String checkPassword(@PathVariable Long id, @RequestParam String password,
                                RedirectAttributes redirectAttributes) {
        try {
            scheduleService.checkPassword(id, password);
            return "redirect:/schedule/" + id + "/edit";
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/schedule/" + id;
        }
    }

}
