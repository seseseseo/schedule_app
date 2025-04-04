package com.example.schedule.controller;

import com.example.schedule.domain.entity.Schedule;
import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.dto.UserResponseDto;
import com.example.schedule.exception.CustomException;
import com.example.schedule.exception.ErrorCode;
import com.example.schedule.repository.ScheduleRepository;
import com.example.schedule.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Log4j2
public class ScheduleController {
    private ScheduleService scheduleService;
    private ScheduleRepository scheduleRepository;
    //레파지토리 직접 사용함 나중에 수정해야댐
    public ScheduleController(ScheduleService scheduleService, ScheduleRepository scheduleRepository) {
        this.scheduleService = scheduleService;
        this.scheduleRepository = scheduleRepository;
    }

    // 일정 등록 폼
    @GetMapping("/schedule/new")
    public String newScheduleForm(HttpServletRequest request,Model model) {
        model.addAttribute("scheduleForm", new ScheduleRequestDto());
        UserResponseDto loginUser = (UserResponseDto) request.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("loginUser", loginUser);
        return "schedule/new";
    }

    // 일정 등록 처리
    @PostMapping("/schedule/new")
    public String newSchedule(@Valid @ModelAttribute("scheduleForm") ScheduleRequestDto scheduleRequestDto,
                              BindingResult bindingResult,
                              HttpServletRequest request, Model model) {

        UserResponseDto loginUser = (UserResponseDto) request.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginUser", loginUser);
            return "schedule/new";
        }
        scheduleService.saveWithUser(scheduleRequestDto, loginUser.getId());
        return "redirect:/schedule/list";
    }

//    @GetMapping("/schedule/list")
//    public String schedulesList(HttpServletRequest request, Model model) {
//        UserResponseDto loginUser = (UserResponseDto) request.getAttribute("loginUser");
//        if (loginUser != null) {
//            model.addAttribute("loginUser", loginUser);
//        }
//        model.addAttribute("schedules",scheduleService.findAll());
//        return "schedule/list";
//    }
    @GetMapping("/schedule/list")
    public String getPagedSchedule(@PageableDefault(size = 10, sort = "lastModifiedDate", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<ScheduleResponseDto> page = scheduleService.findAll(pageable);
        model.addAttribute("page", page);
        return "schedule/list";

    }

    // 단건 조회
    @GetMapping("/schedule/{id}")
    public String scheduleDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        ScheduleResponseDto schedule = scheduleService.findById(id);
        model.addAttribute("schedule", schedule);

        UserResponseDto loginUser = (UserResponseDto) request.getAttribute("loginUser");
        Long loginUserId = (loginUser != null) ? loginUser.getId() : null;

        model.addAttribute("loginUserId", loginUserId);
        return "schedule/detail";
    }
    //수정
    @GetMapping("/schedule/{id}/edit")
    public String editSchedule(@PathVariable Long id,HttpServletRequest request,
                               Model model, RedirectAttributes redirectAttributes) {
        Schedule schedule = scheduleRepository.findByIdWithUser(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        UserResponseDto loginUser = (UserResponseDto) request.getAttribute("loginUser");
        if (!schedule.getUser().getId().equals(loginUser.getId())) {
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

       UserResponseDto loginUser = (UserResponseDto) request.getAttribute("loginUser");
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
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            scheduleService.deleteScheduleWithPassword(id, password);
            return "redirect:/schedule/list";
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/schedule/" + id + "/edit";
        }
    }
}
