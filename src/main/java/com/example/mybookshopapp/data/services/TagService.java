package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.dto.TagCountDto;
import com.example.mybookshopapp.data.repositories.BookTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author karl
 */

@Service
public class TagService {

    private final BookTagRepository bookTagRepository;

    @Autowired
    public TagService(BookTagRepository bookTagRepository) {
        this.bookTagRepository = bookTagRepository;
    }

    public List<TagCountDto> getTagsWithCounts() {
        List<TagCountDto> tagCountDtoList = new ArrayList<>();
        bookTagRepository.findAll().forEach(tagEntity -> tagCountDtoList.add(new TagCountDto(tagEntity)));
        return tagCountDtoList;
    }
}
