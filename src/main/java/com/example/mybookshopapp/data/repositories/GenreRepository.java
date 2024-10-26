package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author karl
 */

public interface GenreRepository extends JpaRepository<GenreEntity, Integer> {
    GenreEntity findBySlugLike(String slug);
    List<GenreEntity> findByParentIdNull();

    @Query(nativeQuery = true, value = "select t.* from genre t where id = :parentId")
    GenreEntity findParentById(@Param("parentId") Integer parentId);
}
