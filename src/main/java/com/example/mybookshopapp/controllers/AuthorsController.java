package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.dto.BooksPageDto;
import com.example.mybookshopapp.data.dto.UserDto;
import com.example.mybookshopapp.data.dto.cookie.BookIds;
import com.example.mybookshopapp.data.model.author.AuthorEntity;
import com.example.mybookshopapp.data.dto.SearchWordDto;
import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.services.AuthorService;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.TransactionsService;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author karl
 */

@Controller
@Api("authors data")
public class AuthorsController extends FatherController{

    private final AuthorService authorsService;

    @Autowired
    public AuthorsController(BookstoreUserRegister userRegister,
                             CookieHandlerService cookieHandler,
                             Convertor convertorService,
                             BookService bookService,
                             JWTBlacklistService blacklistService,
                             TransactionsService transactionsService,
                             AuthorService authorsService) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
        this.authorsService = authorsService;
    }

    @ModelAttribute("authorsMap")
    public Map<String, List<AuthorEntity>> authorMap() {
        return authorsService.getAuthorsMap();
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @GetMapping("/authors")
    public String authorsPage(@CookieValue(value = "cartContents", required = false) String cartContents,
                              @CookieValue(value = "postponedContents", required = false) String postponedContents,
                              Model model) {
        getBookIds(cartContents, postponedContents, model);
        return "/authors/index";
    }

    @ApiOperation("method to get map of authors")
    @GetMapping("/api/authors")
    @ResponseBody
    public Map<String, List<AuthorEntity>> authors() {
        return authorsService.getAuthorsMap();
    }

    @GetMapping("/authors/{slug}")
    public String slugPage(@PathVariable("slug") String slug,
                           @CookieValue(value = "cartContents", required = false) String cartContents,
                           @CookieValue(value = "postponedContents", required = false) String postponedContents,
                           Model model) {
        int curUserId;
        if (model.getAttribute("curUser") instanceof UserDto curUserDto) {
            curUserId = curUserDto.getId();
        } else {
            curUserId = 0;
        }
        BookIds bookIds = getBookIds(cartContents, postponedContents, model);
        AuthorEntity authorEntity = authorsService.getAuthorEntityBySlug(slug);
        model.addAttribute("authorDto", getConvertorService().convertEntityToDto(authorEntity));
        Page<BookEntity> pageAuthorBooks = getBookService().getPageOfBooksByAuthorId(authorEntity.getId(), 0, 6);
        model.addAttribute("totalAuthorBooks", pageAuthorBooks.getTotalElements());
        model.addAttribute("authorBooks", getConvertorService().convertEntityToDto(
                pageAuthorBooks.getContent(),
                bookIds,
                curUserId));
        return "/authors/slug";
    }

    @ApiOperation("method get page of books by author on author_id in path variable order by date of publication")
    @GetMapping("/author/{ID}")
    @ResponseBody
    public BooksPageDto getBooksPageAuthor(@RequestParam("offset") Integer offset,
                                           @RequestParam("limit") Integer limit,
                                           @PathVariable("ID") Integer authorId,
                                           @CookieValue(value = "cartContents", required = false) String cartContents,
                                           @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                           Model model) {
        int curUserId;
        if (model.getAttribute("curUser") instanceof UserDto curUserDto) {
            curUserId = curUserDto.getId();
        } else {
            curUserId = 0;
        }
        BookIds bookIds = getBookIds(cartContents, postponedContents, model);
        return new BooksPageDto(
                getConvertorService().convertEntityToDto(
                        getBookService().getPageOfBooksByAuthorId(authorId, offset, limit).getContent(),
                        bookIds,
                        curUserId));
    }
}
