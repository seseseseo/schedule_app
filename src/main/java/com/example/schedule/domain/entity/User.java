package com.example.schedule.domain.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity // 해당 클래스가 엔티티임을 명시하기 위한 어노테이션, 클래스 자체는 테이블과 일대일로 매칭됨
@Table(name = "users") // 클래스는 테이블과 매핑됨
// users로 바꾸는게 좋음
@Getter //보일러플레이트 코드(반복되는 코드의 작성을 생략하기 위해 롬북을 적용함
// NoArgusConstructor : 매개변수가 없는 생성자를 자동 생성한다.
// AllArgsConstructor : 모든 필드를 매개변수로 갖는 생성자를 자동 생성한다.
// RequiredArgsConstructor : 필드 중 final이나 @NotNull이 설정된 변수를 매개변수로 갖는 생성자를 자동 생성한다.
@NoArgsConstructor(access = AccessLevel.PROTECTED) //외부에서 new User하는 것을 막기 위해 생성자의 accessLevel을 protected로 함
public class User extends Base {
    @Id // 테이블의 기본값 역할로 사용, 모든 엔티티는 이 어노테이션이 필요함
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 해당 필드의 값을 어떤 방식으로 자동으로 생성할지 결정할 때 사용한다.
    // IDENTITY는 기본값 생성을 데이터베이스에 위임하는 방식이다.
    @Column(name = "user_id") //필드는 자동으로 테이블 컬럼으로 매핑됨
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
    // 정적 팩토리 메서드 : static 키워드를 사용해, 객체를 생성하는 정적인 메서드
    // new 키워드 없이 객체 생성이 가능하단 점
    public static User of(String username, String password, String email) {
        return new User(username, password, email);
    }

    // 사용자 이름을 변경하는 명확한 책임을 가지는 메서드
    // 엔티티에서  setter를 사용을 최소화 하고 싶어서 이렇게 메서드를 작성함
    // 불변성을 지키기 위함, setUsername보다 updateUsername이 무엇을 하는지 더 명확함
    public void updateUsername(String username) {
        this.username = username;
    }



}
