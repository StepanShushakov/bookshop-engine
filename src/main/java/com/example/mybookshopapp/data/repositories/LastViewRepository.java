package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.book.links.LastViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author karl
 */

@Repository
public interface LastViewRepository extends JpaRepository<LastViewEntity, Integer> {

    LastViewEntity findLastBooksEntityByUserIdAndBookId(Integer userId, Integer bookId);

}
