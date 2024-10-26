package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.dto.*;
import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.BooksRatingAndPopularityService;
import com.example.mybookshopapp.data.services.TransactionsService;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.services.TagService;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.errs.EmptySearchException;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author karl
 */

@Controller
public class MainPageController extends FatherController {

    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final TagService tagService;

    @Autowired
    public MainPageController(BookstoreUserRegister userRegister,
                              CookieHandlerService cookieHandler,
                              JWTBlacklistService blacklistService,
                              Convertor convertorService,
                              BookService bookService,
                              TransactionsService transactionsService,
                              BooksRatingAndPopularityService booksRatingAndPopularityService,
                              TagService tagService
                              ) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.tagService = tagService;
    }

    @ModelAttribute("recommendedBooks")
    public List<BookDto> recommendedBooks(@CookieValue(value = "cartContents", required = false) String cartContents,
                                          @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                          Model model) {
        return getConvertorService().convertEntityToDto(getBookService()
                        .getPageOfRecommendedBooks(getCurrentUserId(), 0, 6)
                        .getContent(),
                getBookIds(cartContents, postponedContents, model),
                getCurrentUserId());
    }

    @ModelAttribute("popularBooks")
    public List<BookDto> popularBooks(@CookieValue(value = "cartContents", required = false) String cartContents,
                                      @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                      Model model) {
        return getConvertorService().convertEntityToDto(booksRatingAndPopularityService
                        .getPageOfPopularBooks(0, 6)
                        .getContent(),
                getBookIds(cartContents, postponedContents, model),
                getCurrentUserId());
    }

    @ModelAttribute("recentBooks")
    public List<BookDto> recentBooks(@CookieValue(value = "cartContents", required = false) String cartContents,
                                     @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                     Model model){
        return getConvertorService().convertEntityToDto(getBookService()
                        .getPageOfRecentBooks(0, 6)
                        .getContent(),
                getBookIds(cartContents, postponedContents, model),
                getCurrentUserId());
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("searchResults")
    public List<BookDto> searchResults() {
        return new ArrayList<>();
    }

    @ModelAttribute("tagCountDtoList")
    public List<TagCountDto> tagCountDtoList() {
        return tagService.getTagsWithCounts();
    }

    @ModelAttribute(name = "bookCart")
    public List<BookDto> bookCart() {
        return new ArrayList<>();
    }

    @ModelAttribute(name = "bookPostponed")
    public List<BookDto> bookPostponed() {
        return new ArrayList<>();
    }


    @GetMapping("/")
    public String mainPage(@CookieValue(value = "cartContents", required = false) String cartContents,
                           @CookieValue(value = "postponedContents", required = false) String postponedContents,
                           Model model) {
        getBookIds(cartContents, postponedContents, model);
        return "index";
    }

    @GetMapping("about")
    public String about(@CookieValue(value = "cartContents", required = false) String cartContents,
                        @CookieValue(value = "postponedContents", required = false) String postponedContents,
                        Model model) {
        getBookIds(cartContents, postponedContents, model);
        return "about";
    }

    @GetMapping("/tags/{name}")
    public String tags(@PathVariable(value = "name", required = false) TagNameDto tagNameDto,
                       @CookieValue(value = "cartContents", required = false) String cartContents,
                       @CookieValue(value = "postponedContents", required = false) String postponedContents,
                       Model model) {
        model.addAttribute("tagNameDto", tagNameDto);
        model.addAttribute("booksByTag",
                getConvertorService().convertEntityToDto(getBookService()
                        .getPageOfBooksByTagName(tagNameDto.getName(), 0, 20)
                        .getContent(),
                        getBookIds(cartContents, postponedContents, model),
                        getCurrentUserId()));
        return "/tags/index";
    }

    @GetMapping("/recommended")
    @ResponseBody
    public BooksPageDto getBooksPageRecommended(@RequestParam("offset") Integer offset,
                                                @RequestParam("limit") Integer limit,
                                                @CookieValue(value = "cartContents", required = false) String cartContents,
                                                @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                                Model model) {
        int userId = getCurrentUserId();
        return new BooksPageDto(
                getConvertorService().convertEntityToDto(
                        getBookService()
                                .getPageOfRecommendedBooks(userId, offset, limit)
                                .getContent(),
                        getBookIds(cartContents, postponedContents, model),
                        userId));
    }

    @GetMapping("/recent")
    @ResponseBody
    public BooksPageDto getBooksPageRecent(@RequestParam("offset") Integer offset,
                                           @RequestParam("limit") Integer limit,
                                           @CookieValue(value = "cartContents", required = false) String cartContents,
                                           @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                           Model model) {
        return new BooksPageDto(
                getConvertorService().convertEntityToDto(
                        getBookService()
                                .getPageOfRecentBooks(offset, limit)
                                .getContent(),
                        getBookIds(cartContents, postponedContents, model),
                        getCurrentUserId()));
    }

    @GetMapping("/popular")
    @ResponseBody
    public BooksPageDto getBooksPagePopular(@RequestParam("offset") Integer offset,
                                            @RequestParam("limit") Integer limit,
                                            @CookieValue(value = "cartContents", required = false) String cartContents,
                                            @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                            Model model) {
        return new BooksPageDto(
                getConvertorService().convertEntityToDto(booksRatingAndPopularityService
                        .getPageOfPopularBooks(offset, limit)
                        .getContent(),
                        getBookIds(cartContents, postponedContents, model),
                        getCurrentUserId()));
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String getSearchResults(@PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                   @CookieValue(value = "cartContents", required = false) String cartContents,
                                   @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                   Model model) throws EmptySearchException {
        if (searchWordDto != null) {
            model.addAttribute("searchWordDto", searchWordDto);
            Page<BookEntity> page = getBookService().getPageOfSearchResultBooks(searchWordDto.getExample(), 0, 20);
            model.addAttribute("searchCount", page.getTotalElements());
            model.addAttribute("searchResults", getConvertorService().convertEntityToDto(
                    page
                            .getContent(),
                    getBookIds(cartContents, postponedContents, model),
                    getCurrentUserId()));
            return "/search/index";
        } else {
            addBooksCountModeleAttributeFromCookieContents(cartContents, postponedContents, model);
            throw new EmptySearchException("Поиск по null невозможен");
        }
    }

    @GetMapping("/search/page/{searchWord}")
    @ResponseBody
    public BooksPageDto getNextSearchPage(@RequestParam("offset") Integer offset,
                                          @RequestParam("limit") Integer limit,
                                          @PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                          @CookieValue(value = "cartContents", required = false) String cartContents,
                                          @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                          Model model) {
        Page<BookEntity> page = getBookService().getPageOfSearchResultBooks(searchWordDto.getExample(), offset, limit);
        BooksPageDto booksPageDto = new BooksPageDto(getConvertorService().convertEntityToDto(
                page
                        .getContent(),
                getBookIds(cartContents, postponedContents, model),
                getCurrentUserId()));
        booksPageDto.setCount((int) page.getTotalElements());
        return booksPageDto;

//        return new BooksPageDto(
//                getConvertorService().convertEntityToDto(getBookService()
//                        .getPageOfGoogleBooksApiSearchResult(searchWordDto.getExample(), offset, limit)));
    }
}
