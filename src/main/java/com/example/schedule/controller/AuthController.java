package com.example.schedule.controller;

import com.example.schedule.dto.LoginRequestDto;
import com.example.schedule.dto.UserResponseDto;
import com.example.schedule.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Controller
//로그인 로그아웃
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    //로그인 폼
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginRequestDto());
        return "login";
    }

    //로그인 처리
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginRequestDto loginForm,
                        BindingResult bindingResult,
                        HttpServletRequest request,
                        Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        Optional<UserResponseDto> optionalUser = userService.login(loginForm.getEmail(), loginForm.getPassword());

        if (optionalUser.isEmpty()) {
            model.addAttribute("loginError", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return "login";
        }
        UserResponseDto user = optionalUser.get();
        request.getSession(true).setAttribute("userId", user.getId());
        return "redirect:/";
    }
    @PostMapping("/logout")
    public String logoutPost(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
