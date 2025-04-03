package com.example.schedule.dto;

import com.example.schedule.exception.ErrorCode;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Builder
@Data
public class ErrorDto {
    public int status;
    public String message;
    public String code;

    public static ResponseEntity<ErrorDto> toResponseEntity(ErrorCode e) {
        return ResponseEntity.status(e.getStatus().value())
                .body(ErrorDto.builder()
                        .status(e.getStatus().value())
                        .code(e.getCode())
                        .message(e.getMessage())
                        .build());
    }

}
