package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.other.FaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author karl
 */

@Repository
public interface FaqRepository extends JpaRepository<FaqEntity, Integer> {

    List<FaqEntity> findAllByOrderBySortIndexAsc();
}
