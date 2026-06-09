package br.com.ccgl.sunharvestbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

import java.util.Date;

@MappedSuperclass
@Getter
public abstract class BaseEntity {

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private Date createdAt;

    @PrePersist
    protected void prePersist() {
        this.createdAt = new Date();
    }
}
