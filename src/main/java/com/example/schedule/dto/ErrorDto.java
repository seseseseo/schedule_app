package com.example.schedule.dto;

import com.example.schedule.exception.ErrorCode;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Builder //빌더 패턴을 자동으로 만들어주는 어노테이션
@Data // 롬북의 종합세트 @Getter @Setter @ToString @EqualsAndHashCode @RequiredArgsConstructor
public class ErrorDto {
    public int status; // http 상태 코드 400, 404, 500 등등
    public String message; // 클라이언트에 전달할 에러메세지
    public String code; // 내부에 정의한 에러 코드

    //정적 팩토리 메서드
    // 에러코드(enum)을 받아서 api응답용 에러 객체 생성을 위해 ResponseEntity<ErrorDto>로 감쌈
    public static ResponseEntity<ErrorDto> toResponseEntity(ErrorCode e) {
        return ResponseEntity.status(e.getStatus().value())
                .body(ErrorDto.builder()
                        .status(e.getStatus().value())
                        .code(e.getCode())
                        .message(e.getMessage())
                        .build());
    }

}
