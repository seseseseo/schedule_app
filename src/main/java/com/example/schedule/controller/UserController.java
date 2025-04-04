package com.example.schedule.controller;

import com.example.schedule.config.PasswordEncoder;
import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.dto.UserRequestDto;
import com.example.schedule.dto.UserResponseDto;
import com.example.schedule.exception.CustomException;
import com.example.schedule.exception.ErrorCode;
import com.example.schedule.service.ScheduleService;
import com.example.schedule.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Log4j2
public class UserController {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    //회원가입 폼
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRequestDto());
        return "register";
    }

    //회원가입 처리
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserRequestDto user,
                           BindingResult bindingResult,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        Long userId = userService.register(user);
        request.getSession(true).setAttribute("userId", userId);
        redirectAttributes.addFlashAttribute("message", true);
        return "redirect:/";
    }


    // 유저 수정 폼
    @GetMapping("/users/{id}")
    public String userEditForm(@PathVariable Long id, HttpServletRequest request, Model model) {
        UserResponseDto loginUser = (UserResponseDto) request.getAttribute("loginUser");

        if (loginUser == null || !loginUser.getId().equals(id)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        model.addAttribute("userForm", loginUser);
        return "userEdit";
    }
    //유저수정 처리
    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("userForm") UserRequestDto user,
                             BindingResult bindingResult,
                             @RequestParam("password") String password,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        log.info("폼 요청 도착");
        UserResponseDto loginUser = (UserResponseDto) request.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getId().equals(id)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (bindingResult.hasErrors()) {
            return "userEdit";
        }
        try {

            userService.updateUserWithPasswordCheck(id, user, password);
            request.getSession(true).setAttribute("userId", id);
            redirectAttributes.addFlashAttribute("message", true);
            return "redirect:/";
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/" + id;
        }
    }
}
