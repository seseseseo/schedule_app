package com.example.schedule.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;


@EntityListeners(AuditingEntityListener.class)
@Getter
@MappedSuperclass
public class BaseTime {
    @CreatedDate
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdDate;
    //이부분 필드 이름 수정
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime lastModifiedDate;


}
