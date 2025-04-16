package com.example.schedule.filter;


import com.example.schedule.config.LoginProperties;
import com.example.schedule.domain.entity.User;
import com.example.schedule.dto.UserResponseDto;
import com.example.schedule.exception.CustomException;
import com.example.schedule.exception.ErrorCode;
import com.example.schedule.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;


@Log4j2
@RequiredArgsConstructor // 초기화 되지 않은 final 필드에 대해 생성자를 만들어줌
//ㅇㅣ걸 쓰고싶은데 value는 final 필드가 불가능 하다고해서 다른 방법을 찾음
public class LoginCheckFilter implements Filter {
    //filter인터페이스를 구현한 커스텀 필터
    // http요청이 컨트롤러에 가기 전에 필터가 먼저 요청을 가로채서 처리할 수 있다
    private final UserRepository userRepository;
    private final LoginProperties loginProperties; //yml에 정의한 경로를 바인딩해주는 설정 클래스
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI(); // 요청한 URl을 가져와서 로그인 체크에 사용
        // 이부분이 하드 코딩이라고 생각함! + if문 열거
//        if(requestURI.startsWith("/login") || requestURI.contains("/register") ||
//
//                requestURI.startsWith("/css") ||
//                requestURI.startsWith("/js") ||
//                requestURI.startsWith("/schedule/list") ||
//                requestURI.startsWith("/images")) {
//            chain.doFilter(request, response);
//            return;
//        }

        if (isExcludedPath(requestURI)) { //예외 경로일 경우 로그인 체크 없이 컨트롤러로 넘어감
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);//기존 세션을 가져온다. 없으면 새로 생성하지 않는다
        if (session != null && session.getAttribute("userId") != null) {
            //세션이 존재하고 로그인한 사용자 id가 저장되어있다면 해당id를 가져온다
            Long userId = (Long) session.getAttribute("userId");
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            // 만약 해당 id로 user를 조회하고 db에 없으면 예외를 던짐

            httpRequest.setAttribute("loginUser", new UserResponseDto(user));
            // 컨트롤러에서 세션을 바로 사용할 수 있게 loginUser에 user 정보를 담아둠
        }
        chain.doFilter(request, response);
    }
    private boolean isExcludedPath(String path) {
        return loginProperties.getExcludePaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
        //경로 매칭을 위해 사용함
    }
}
