package com.example.schedule.config;


import com.example.schedule.domain.entity.User;
import com.example.schedule.dto.UserResponseDto;
import com.example.schedule.exception.CustomException;
import com.example.schedule.exception.ErrorCode;
import com.example.schedule.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
@Log4j2
public class LoginCheckFilter implements Filter {

    private final UserRepository userRepository;

    public LoginCheckFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        if(requestURI.startsWith("/login") || requestURI.contains("/register") ||

                requestURI.startsWith("/css") ||
                requestURI.startsWith("/js") ||
                requestURI.startsWith("/schedule/list") ||
                requestURI.startsWith("/images")) {
            chain.doFilter(request, response);
            return;
        }
        // 세션에서 사용자 id를찾고 없으면 null 반환
        HttpSession session = httpRequest.getSession(false);

        if (session != null && session.getAttribute("userId") != null) {
            Long userId = (Long) session.getAttribute("userId");
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            UserResponseDto loginUser = new UserResponseDto(user);
            httpRequest.setAttribute("loginUser", loginUser);

        }



        chain.doFilter(request, response);
    }
}
