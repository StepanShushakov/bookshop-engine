package com.example.mybookshopapp.data.model.payments;

import com.example.mybookshopapp.data.model.enums.InvoiceState;
import com.example.mybookshopapp.data.model.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author karl
 */

@Entity
@Table(name = "invoice")
@Getter
@Setter
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition = "INT NOT NULL")
    private UserEntity user;

    @Column(name = "contents", nullable = false, columnDefinition = "TEXT")
    private String contents;

    private Integer sum;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceState state;

}
