package com.example.schedule.domain.entity;

import com.example.schedule.config.PasswordEncoder;
import com.example.schedule.dto.UserResponseDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user") // user는 예약어일 수도 있으니 테이블 명시해줌
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //외부에서 new User하는 것을 막기 위해 생성자의 accessLevel을 protected로 함
public class User extends Base {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // 이메일은 unique 해야함으로.
    @Column(nullable = false, unique = true)
    private String email;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    //정적 팩토리 메서드로 객체를 생성
    public static User of(String username, String password, String email) {
        return new User(username, password, email);
    }

    // 사용자 이름을 변경하는 명확한 책임을 가지는 메서드
    // setter를 사용하고 싶지 않아서 이렇게 메서드를 작성함
    public void updateUsername(String username) {
        this.username = username;
    }

    //비밀번호 확인로직은 사용되지 않아서 지움

}
