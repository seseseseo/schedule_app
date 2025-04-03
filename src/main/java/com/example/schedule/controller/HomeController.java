package com.example.schedule.controller;

import com.example.schedule.dto.UserRequestDto;
import com.example.schedule.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String home(HttpSession session, Model model) {
        log.info("home controller");
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                userService.findById(userId).ifPresent(user -> model.addAttribute("loginUser", user));
            }
        }
        return "home";
    }
    //회원가입 폼
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRequestDto());
        return "register";
    }
    //로그인 폼
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new UserRequestDto());
        return "login";
    }

    // 일정 등록 폼
    @GetMapping("/schedule/new")
    public String newScheduleForm(Model model) {
        return "schedule/new";
    }

    @GetMapping("/schedule/list")
    public String schedulesList(Model model) {
        return "schedule/list";
    }

}
