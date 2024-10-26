package com.example.mybookshopapp.security.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author karl
 */

@Entity
@Table(name = "sms_keys")
@Getter
@Setter
public class SmsCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @Column(name = "expire_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime expireTime;

    public SmsCode(String code, Integer expireIn) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public SmsCode(){}

    public Boolean isExpired(){
        return LocalDateTime.now().isAfter(expireTime);
    }
}
