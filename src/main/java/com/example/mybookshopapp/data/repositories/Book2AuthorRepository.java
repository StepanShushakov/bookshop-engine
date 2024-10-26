package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.book.links.Book2AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Book2AuthorRepository extends JpaRepository<Book2AuthorEntity, Integer> {
    void deleteAllByBookId(Integer id);
}