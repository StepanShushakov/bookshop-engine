package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.other.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author karl
 */

@Repository
public interface DocumentsRepository extends JpaRepository<DocumentEntity, Integer> {

    List<DocumentEntity> findAllByOrderBySortIndexAsc();

    DocumentEntity findDocumentEntitiesBySlug(String slug);
}
