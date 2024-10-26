package com.example.mybookshopapp.data.model.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author karl
 */

@Entity
@Table(name = "jwt_blacklist")
@Getter
@Setter
public class JWTBlacklistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String token;

    @Override
    public String toString() {
        return "JwtBlacklist{" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
