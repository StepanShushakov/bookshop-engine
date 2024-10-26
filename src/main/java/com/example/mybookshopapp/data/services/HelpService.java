package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.model.other.FaqEntity;
import com.example.mybookshopapp.data.repositories.FaqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author karl
 */

@Service
public class HelpService {

    private final FaqRepository faqRepository;

    @Autowired
    public HelpService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<FaqEntity> getAllSortedFaqs() {
        return faqRepository.findAllByOrderBySortIndexAsc();
    }
}
