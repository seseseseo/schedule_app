package com.example.schedule.exception;

import com.example.schedule.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorDto> globalExceptionHandler(CustomException e) {
        return ErrorDto.toResponseEntity(e.getErrorCode());
    }
}
