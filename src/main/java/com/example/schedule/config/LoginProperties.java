package com.example.schedule.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "login")
@Getter
@Setter
public class LoginProperties {
    private List<String> excludePaths;
}

// 로그인 필터를 만들었는데 하드코딩된 URl가 너무 많음
// 외부에서 관리하면 좋겠다고 생각해서 application.properties에 @Value로 넣어보려고 함
//
//@Value("${login.exclude-paths}")
//private List<String> excludePaths;
//단일 변수에만 값을 주입할 수 있고 final 키워드도 불가능, 변수마다 Value 붙여야 함
//
//그래서 @ConfigurationProperties  방식으로 클래스 단위로 관리해보려고 함
//그러면 properties -> yml 이 좋을 듯 해서!
// 이건 설정을 주입하는 클래싀고 필터랑은 역할이 다르니 필터와 패키지를 분리해서 필터는 필터 요청만 처리, 설정 클래스는 설정만 하게

// https://blog.yevgnenll.me/posts/spring-configuration-properties-fetch-application-properties 참고

