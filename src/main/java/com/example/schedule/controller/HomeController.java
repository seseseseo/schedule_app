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

    @RequestMapping("/")
    public String home(HttpServletRequest request, Model model) {
        log.info("home controller");
        HttpSession session = request.getSession(false);
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

    //회원가입 처리
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserRequestDto user,
                           BindingResult bindingResult,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        log.info("DEBUG - UserRequestDto: username={}", user.getUsername());
        log.info("DEBUG - UserRequestDto: email={}", user.getEmail());
        log.info("DEBUG - UserRequestDto: password={}", user.getPassword());

        if (bindingResult.hasErrors()) {
            return "register";
        }

        Long userId = userService.register(user);
        request.getSession(true).setAttribute("userId", userId);
        UserResponseDto newUser = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
       request.setAttribute("loginUser", newUser);

        return "redirect:/";
    }

    //로그인 폼
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginRequestDto());
        return "login";
    }

    //로그인 처리
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginRequestDto user,
                        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        try {
            Long userId = userService.login(user.getEmail(), user.getPassword())
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PASSWORD));
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", userId);
            UserResponseDto loginUser = userService.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            session.setAttribute("loginUser", loginUser);
            return "redirect:/";
        } catch (Exception e) {
            log.error("로그인 중 예외 발생: {}", e.getMessage(), e);
            throw e;
        }

    }
    // 유저 수정 폼
    @GetMapping("/users/{id}")
    public String userEditForm(@PathVariable Long id, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        Long userId = (session != null) ? (Long) session.getAttribute("userId") : null;
        if (userId == null || !userId.equals(id)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        UserResponseDto user = userService.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        model.addAttribute("userForm", user);
        return "userEdit";
    }
    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("userForm") UserRequestDto user,
                             BindingResult bindingResult,
                             @RequestParam("password") String password,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        log.info("폼 요청 도착"); // ❗여기서 안 뜨면 아예 매핑 실패
        HttpSession session = request.getSession(false);
        Long userId = (session != null) ? (Long) session.getAttribute("userId") : null;


        if (userId == null || !userId.equals(id)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (bindingResult.hasErrors()) {
            return "userEdit";
        }
        try {
            userService.updateUserWithPasswordCheck(id, user, password); // 👈 검증 포함
            UserResponseDto updatedUser = userService.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            session.setAttribute("loginUser", updatedUser);
            redirectAttributes.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
            return "redirect:/";
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/" + id;
        }
    }
    // 일정 등록 폼
    @GetMapping("/schedule/new")
    public String newScheduleForm(Model model) {
        model.addAttribute("scheduleForm", new ScheduleRequestDto());
        return "schedule/new";
    }

    // 일정 등록 처리
    @PostMapping("/schedule/new")
    public String newSchedule(@Valid @ModelAttribute("scheduleForm") ScheduleRequestDto scheduleRequestDto,
                              BindingResult bindingResult,
                              HttpServletRequest request, Model model) {
        if (bindingResult.hasErrors()) {
            log.warn("❗ 유효성 검사 실패: {}", bindingResult.getAllErrors());
            return "schedule/new";
        }
        HttpSession session = request.getSession(false); //현재 로그인한 사용자의 세션 정보를 가져옴
        if (session == null || session.getAttribute("userId") == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        Long userId = (Long) session.getAttribute("userId");
        scheduleService.saveWithUser(scheduleRequestDto, userId);
        return "redirect:/schedule/list";

    }

    @GetMapping("/schedule/list")
    public String schedulesList(Model model) {
        List<ScheduleResponseDto> schedules = scheduleService.findAll();
        model.addAttribute("schedules", schedules);
        return "schedule/list";
    }

    // 단건 조회
    @GetMapping("/schedule/{id}")
    public String scheduleDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        ScheduleResponseDto schedule = scheduleService.findById(id);
        model.addAttribute("schedule", schedule);
        HttpSession session = request.getSession(false);
        if (session != null) {
            model.addAttribute("loginUserId", session.getAttribute("userId"));
        }
        return "schedule/detail";
    }

    @PostMapping("/logout")
    public String logoutPost(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
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
    //수정
    @GetMapping("/schedule/{id}/edit")
    public String editSchedule(@PathVariable Long id,HttpServletRequest request,

                               Model model, RedirectAttributes redirectAttributes) {
        Schedule schedule = scheduleRepository.findByIdWithUser(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        HttpSession session = request.getSession(false);
        Long sessionUserId = (Long) session.getAttribute("userId");

        if (!schedule.getUser().getId().equals(sessionUserId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        model.addAttribute("scheduleForm", new ScheduleRequestDto(schedule));
        return "schedule/edit";
    }
    // 수정 처리
    @PostMapping("/schedule/{id}/edit")
    public String updateSchedule(@PathVariable Long id,
                                 @Valid @ModelAttribute("scheduleForm") ScheduleRequestDto dto,
                                 BindingResult bindingResult,
                                 @RequestParam String password,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "schedule/edit";
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Long sessionUserId = (Long) session.getAttribute("userId");
        try {
            scheduleService.updateSchedule(id, dto, password);
            return "redirect:/schedule/list";
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/schedule/" + id + "/edit";
        }
    }
    @PostMapping("/schedule/{id}/delete")
    public String deleteSchedule(@PathVariable Long id,
                                 @RequestParam String password,
                                 RedirectAttributes redirectAttributes) {
        try {
            scheduleService.deleteSchedule(id, password);
            return "redirect:/schedule/list";
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/schedule/" + id + "/edit";
        }
    }
}
