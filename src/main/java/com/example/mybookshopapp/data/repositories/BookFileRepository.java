package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.book.BookFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author karl
 */

public interface BookFileRepository extends JpaRepository<BookFileEntity, Integer> {

    BookFileEntity findBookFileEntitiesByHash(String hash);
}
