package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.dto.GenreDto;

import java.util.Comparator;

/**
 * @author karl
 */

public class GenreComparator implements Comparator<GenreDto> {
    @Override
    public int compare(GenreDto a, GenreDto b) {
        return b.getBooksCount().compareTo(a.getBooksCount());
    }
}
