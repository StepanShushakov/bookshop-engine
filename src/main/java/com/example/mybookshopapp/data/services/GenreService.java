package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.dto.GenreDto;
import com.example.mybookshopapp.data.model.genre.GenreEntity;
import com.example.mybookshopapp.data.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author karl
 */

@Service
public class GenreService {
    private GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<GenreDto> getParentsGenres() {
        List<GenreDto> genres = new ArrayList<>();
        for (GenreEntity genre: genreRepository.findByParentIdNull()) {
            genres.add(Convertor.convertGenreEntityToDto(genre));
        };
        return genres;
    }

    public List<GenreEntity> getParentsWay(GenreEntity genre) {
        List<GenreEntity> genres = new ArrayList<>();
        if (genre.getParentId() != null) {
            genres = getParentsWay(new ArrayList<>(), genreRepository.findParentById(genre.getParentId()));
            Collections.reverse(genres);
        };
        return genres;
    }

    private List<GenreEntity> getParentsWay(List<GenreEntity> genres, GenreEntity genre) {
        genres.add(genre);
        if (genre.getParentId() != null) {
            getParentsWay(genres, genreRepository.findParentById(genre.getParentId()));
        }
        return genres;
    }

    public GenreEntity getGenreEntityBySlug(String slug) {
        return genreRepository.findBySlugLike(slug);
    }
}
