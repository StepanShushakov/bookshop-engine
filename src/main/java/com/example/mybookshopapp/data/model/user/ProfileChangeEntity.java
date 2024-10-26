package com.example.mybookshopapp.data.model.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author karl
 */

@Entity
@Table(name = "profile_change")
@Getter
@Setter
public class ProfileChangeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition = "INT NOT NULL")
    private UserEntity user;
    private String name;
    private String phone;
    private String email;
    private String password;
    private String token;
}
