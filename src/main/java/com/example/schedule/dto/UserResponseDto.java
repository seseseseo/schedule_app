package com.example.schedule.dto;


import com.example.schedule.domain.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private Long id;
    private final String username;
    private final String email;


    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();

    }
    public UserResponseDto(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // 다른 객체로부터 새로운 객체를 만들 때
    // 유저로부터 DTO를 만든다

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }


}
