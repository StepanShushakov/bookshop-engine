package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.author.AuthorEntity;
import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author karl
 */

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookRepositoryTests {

    private final BookRepository bookRepository;
    private final Convertor convertor;

    @Autowired
    BookRepositoryTests(BookRepository bookRepository, Convertor convertor) {
        this.bookRepository = bookRepository;
        this.convertor = convertor;
    }

    @Test
    @Transactional
    void findBookEntitiesByAuthorsContaining() {
        String token = "Jelene";
        List<BookEntity> bookListByAuthor = bookRepository.findBookEntitiesByAuthorsContaining(token);

        assertNotNull(bookListByAuthor);
        assertFalse(bookListByAuthor.isEmpty());

        for (BookEntity book : bookListByAuthor) {
            Logger.getLogger(this.getClass().getSimpleName()).info(book.getTitle());
            boolean authorFind = false;
            for (AuthorEntity author: book.getAuthors()) {
                Logger.getLogger(this.getClass().getSimpleName()).info(author.getName());
                authorFind = author.getName().contains(token);
                if (authorFind) {
                    break;
                }
            }
            assertTrue(authorFind);
        }
    }

    @Test
    void findBookEntitiesByTitleContaining() {
        String token = "fish";
        List<BookEntity> bookListByTitle = bookRepository.findBookEntitiesByTitleContaining(token);
        assertNotNull(bookListByTitle);
        assertFalse(bookListByTitle.isEmpty());
        for (BookEntity book: bookListByTitle) {
            Logger.getLogger(this.getClass().getSimpleName()).info(book.getTitle());
            assertTrue(book.getTitle().contains(token));
        }
    }

    @Test
    void getBestsellers() {
        List<BookEntity> bestSellersBooks = bookRepository.getBestsellers();
        assertNotNull(bestSellersBooks);
        assertFalse(bestSellersBooks.isEmpty());

        assertThat(bestSellersBooks.size()).isGreaterThan(1);
    }
}