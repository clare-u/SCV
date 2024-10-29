package com.scv.global.shared;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private boolean deleted = false;

    /**
     * 소프트 삭제
     */
    public void delete() {
        this.deleted = true; // setter 없이 직접 필드 접근
    }

    /**
     * 복원
     */
    public void restore() {
        this.deleted = false; // setter 없이 직접 필드 접근
    }
}
