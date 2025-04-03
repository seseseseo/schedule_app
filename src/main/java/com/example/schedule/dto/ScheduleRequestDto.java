package com.example.schedule.dto;

import com.example.schedule.domain.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequestDto {
    public User user;

    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(max = 10, message = "제목은 10자 이내여야 합니다.")
    public String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    public String content;

}
