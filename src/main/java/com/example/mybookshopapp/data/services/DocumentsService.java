package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.dto.DocumentDto;
import com.example.mybookshopapp.data.repositories.DocumentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author karl
 */

@Service
public class DocumentsService {

    private final DocumentsRepository documentsRepository;

    @Autowired
    public DocumentsService(DocumentsRepository documentsRepository) {
        this.documentsRepository = documentsRepository;
    }

    public List<DocumentDto> getAllSortedDocuments() {
        List<DocumentDto> dtoList = new ArrayList<>();
        documentsRepository.findAllByOrderBySortIndexAsc().forEach(documentEntity -> dtoList.add(new DocumentDto(documentEntity)));
        return dtoList;
    }

    public DocumentDto getDocumentEntityBySlug(String slug) {
        return new DocumentDto(documentsRepository.findDocumentEntitiesBySlug(slug));
    }
}
