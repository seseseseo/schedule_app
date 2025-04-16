package com.example.schedule;

import com.example.schedule.filter.LoginCheckFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(LoginCheckFilter.class)
public class ScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleApplication.class, args);
    }
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("DDD");
    }
}
