package com.example.mybookshopapp.security.repositories;

import com.example.mybookshopapp.data.model.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author karl
 */

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookstoreUserRepositoryTests {

    private final BookstoreUserRepository bookstoreUserRepository;

    @Autowired
    BookstoreUserRepositoryTests(BookstoreUserRepository bookstoreUserRepository) {
        this.bookstoreUserRepository = bookstoreUserRepository;
    }

    @Test
    public void testAddNewUser() {
        UserEntity user = new UserEntity();
        user.setPassword("1234567890");
        user.setHash("123");
        user.setBalance(0);
        user.setRegTime(LocalDateTime.now());
        user.setPhone("56516");
        user.setName("Tester");
        user.setEmail("test@ggg.ru");

        assertNotNull(bookstoreUserRepository.save(user));
    }
}