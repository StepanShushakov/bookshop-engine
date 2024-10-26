package com.example.mybookshopapp.data.model.book;

import com.example.mybookshopapp.data.model.book.file.BookFileTypeEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author karl
 */

@Entity
@Table(name="book_file")
@Getter
@Setter
public class BookFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false, referencedColumnName = "id")
    private BookFileTypeEntity type;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String path;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private BookEntity bookEntity;
}
