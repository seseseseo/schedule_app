package com.example.schedule.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@EntityListeners(AuditingEntityListener.class) //jpa 엔티티에 auditing 을 사용하기 위해서
//이 리스너는 엔티티가 저장/ 수정될 때 생성자/수정자를 자동으로 채워줌
// 등록자//수정자도 실무에선 필요할 듯 해서 따로 BaseTime외에도 이 클래스를 추가함
@Getter
@MappedSuperclass
public class Base extends BaseTime {
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    @LastModifiedBy
    private String lastModifiedBy;
}
