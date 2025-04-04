package com.example.schedule.domain.entity;

import com.example.schedule.config.PasswordEncoder;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Base {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public static User of(String username, String password, String email) {
        return new User(username, password, email);
    }
    public void updateUsername(String username) {
        this.username = username;
    }
    public boolean checkPassword(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.password);
    }

}
