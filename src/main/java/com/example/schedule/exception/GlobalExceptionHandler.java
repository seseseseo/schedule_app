package com.example.schedule.exception;

import com.example.schedule.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @ControllerAdvice + @ResponseBody
// 전역적으로 모든 @RestController 또는 @Controller 에서 발생한 예외를 가르채고 JSON 형태로 응답하게함
// 전역 예외 처리기 역할을 수행하는 클래스 임
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 이 어노테이션에 정의된 메서드는 모든 컨트롤러의 예외를 처리할 수 있다
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorDto> globalExceptionHandler(CustomException e) {
        // HTTP 상태코드 + 응답 본문을 함께 리턴하기 위해 ResponseEntity 사용
        // 응답 바디는 우리가 만든 ErrorDto 클래스
        // CustomException 타입의 예외 객체를 파라미터로 받음
        return ErrorDto.toResponseEntity(e.getErrorCode());
        // 여기서 에러 코드를 추출하여 적절한 에러 응답을 만들어주는 것이 목적
    }
}
