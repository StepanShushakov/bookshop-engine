package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.payments.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author karl
 */

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Integer> {

    InvoiceEntity findInvoiceEntitiesById(Integer id);
}
