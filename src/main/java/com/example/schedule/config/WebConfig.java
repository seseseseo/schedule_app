package com.example.schedule.config;

import com.example.schedule.filter.LoginCheckFilter;
import com.example.schedule.repository.UserRepository;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //spring의 설정 클래스임을 나타냄
@RequiredArgsConstructor //final인 필드에 생성자를 안쓰고 싶어서
public class WebConfig {
    private final UserRepository userRepository;
    private final LoginProperties loginProperties;
    // filter를 스프링 컨테이너에 수동 등록하는 메서드
    @Bean
    public FilterRegistrationBean<Filter> loginCheckFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginCheckFilter(userRepository, loginProperties));
        filterRegistrationBean.addUrlPatterns("/*"); //모든 요청 경로에 적용되도록 설정
        filterRegistrationBean.setOrder(1); //필터 실행 순서를 정함 낮을수록 먼저 실행
        return filterRegistrationBean; //필터 등록 완료하고 스프링에 반환

    }

}
