package com.example.mybookshopapp.data.model.user;

import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.model.book.BookRatingEntity;
import com.example.mybookshopapp.data.model.book.file.FileDownloadEntity;
import com.example.mybookshopapp.data.model.book.review.BookReviewEntity;
import com.example.mybookshopapp.data.model.book.review.BookReviewLikeEntity;
import com.example.mybookshopapp.data.model.book.review.MessageEntity;
import com.example.mybookshopapp.data.model.enums.Provider;
import com.example.mybookshopapp.data.model.payments.BalanceTransactionEntity;
import com.example.mybookshopapp.data.model.payments.InvoiceEntity;
import com.example.mybookshopapp.security.enums.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author karl
 */

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime regTime;

    @Column(columnDefinition = "INT NOT NULL")
    private int balance;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String email;
    private String phone;
    private String password;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @ManyToMany
    @JoinTable(name = "book2user", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<BookEntity> books = new ArrayList<>();

    @OneToMany
    @JoinTable(name = "last_books", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<BookEntity> lastBooks = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<FileDownloadEntity> fileDownloads;

    @OneToMany(mappedBy = "user")
    private List<BalanceTransactionEntity> balanceTransactions;

    @OneToMany(mappedBy = "user")
    private List<UserContactEntity> userContacts;

    @OneToMany(mappedBy = "user")
    private List<MessageEntity> messages;

    @OneToMany(mappedBy = "user")
    private List<BookReviewEntity> bookReviews;

    @OneToMany(mappedBy = "user")
    private List<BookReviewLikeEntity> bookReviewLikes;

    @OneToMany(mappedBy = "user")
    private List<BookRatingEntity> bookRatingEntityList;

    @OneToMany(mappedBy = "user")
    private List<InvoiceEntity> invoices;
}
