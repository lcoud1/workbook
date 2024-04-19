package org.zerock.b01.domain;


import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(value = { AuditingEntityListener.class })
@Getter
abstract class BaseEntity {

    // 엔티티의 생성일을 나타내는 필드
    @CreatedDate
    @Column(name = "regdate", updatable = false)
    private LocalDateTime regDate;

    // 엔티티의 최근 수정일을 나타내는 필드
    @LastModifiedDate
    @Column(name ="moddate" )
    private LocalDateTime modDate;

}
