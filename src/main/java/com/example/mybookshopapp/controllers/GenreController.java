package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.dto.GenreDto;
import com.example.mybookshopapp.data.model.genre.GenreEntity;
import com.example.mybookshopapp.data.dto.SearchWordDto;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.TransactionsService;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.services.GenreService;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

/**
 * @author karl
 */

@Controller
@RequestMapping("/genres")
public class GenreController extends FatherController{

    private final GenreService genreService;

    @Autowired
    public GenreController(BookstoreUserRegister userRegister,
                           CookieHandlerService cookieHandler,
                           JWTBlacklistService blacklistService,
                           Convertor convertorService,
                           BookService bookService,
                           TransactionsService transactionsService,
                           GenreService genreService) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
        this.genreService = genreService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("genresList")
    public List<GenreDto> getGenres() {
        return genreService.getParentsGenres();
    }

    @GetMapping("")
    public String genresPage(@CookieValue(value = "cartContents", required = false) String cartContents,
                             @CookieValue(value = "postponedContents", required = false) String postponedContents,
                             Model model) {
        getBookIds(cartContents, postponedContents, model);
        return "genres/index";
    }

    @GetMapping("/{slug}")
    public String slugPage(@PathVariable("slug") String slug,
                           @CookieValue(value = "cartContents", required = false) String cartContents,
                           @CookieValue(value = "postponedContents", required = false) String postponedContents,
                           Model model) {
        GenreEntity genreEntity = genreService.getGenreEntityBySlug(slug);
        model.addAttribute("slug", slug);
        model.addAttribute("genreBooks",
                getConvertorService().convertEntityToDto(
                        getBookService().getPageOfGenreSlug(slug, 0, 20).getContent(),
                        getBookIds(cartContents, postponedContents, model),
                        getCurrentUserId()));
        model.addAttribute("genresWay",
                genreService.getParentsWay(genreEntity));
        model.addAttribute("currentGenre", genreEntity);
        return "/genres/slug";
    }

}
