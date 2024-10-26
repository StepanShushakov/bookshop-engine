package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.dto.*;
import com.example.mybookshopapp.data.dto.cookie.BookIds;
import com.example.mybookshopapp.data.model.book.review.BookReviewEntity;
import com.example.mybookshopapp.data.services.*;
import com.example.mybookshopapp.data.model.author.AuthorEntity;
import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author karl
 */

@Controller
@RequestMapping("/books")
public class BookController extends FatherController{

    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final AuthorService authorService;
    private final ResourceStorage storage;
    private final BookReviewService bookReviewService;

    @Autowired
    public BookController(BookstoreUserRegister userRegister,
                          CookieHandlerService cookieHandler,
                          JWTBlacklistService blacklistService,
                          Convertor convertorService,
                          BookService bookService,
                          TransactionsService transactionsService,
                          BooksRatingAndPopularityService booksRatingAndPopularityService,
                          AuthorService authorService,
                          ResourceStorage storage,
                          BookReviewService bookReviewService) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.authorService = authorService;
        this.storage = storage;
        this.bookReviewService = bookReviewService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("recentBooks")
    public List<BookDto> recentBooks(@CookieValue(value = "cartContents", required = false) String cartContents,
                                     @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                     Model model) {
        return getConvertorService().convertEntityToDto(
                getBookService()
                .getPageOfRecentBooks(0, 20)
                .getContent(),
                getBookIds(cartContents, postponedContents, model),
                getCurrentUserId());
    }

    @ModelAttribute("popularBooks")
    public List<BookDto> popularBooks(@CookieValue(value = "cartContents", required = false) String cartContents,
                                      @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                      Model model) {
        return getConvertorService().convertEntityToDto(booksRatingAndPopularityService
                .getPageOfPopularBooks(0, 20)
                .getContent(),
                getBookIds(cartContents, postponedContents, model),
                getCurrentUserId());
    }

    @GetMapping("/recent")
    public String recentBooksPage(@CookieValue(value = "cartContents", required = false) String cartContents,
                                  @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                  Model model) {
        getBookIds(cartContents, postponedContents, model);
        return "books/recent";
    }

    @GetMapping("/popular")
    public String popularBooksPage(@CookieValue(value = "cartContents", required = false) String cartContents,
                                   @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                   Model model) {
        getBookIds(cartContents, postponedContents, model);
        return"books/popular";
    }

    @GetMapping("/viewed")
    public String viewedBooksPage(@CookieValue(value = "cartContents", required = false) String cartContents,
                                   @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                   Model model) {
        UserDto curUserDto = (UserDto) model.getAttribute("curUser");
        BookIds bookIds = getBookIds(cartContents, postponedContents, model);
        assert curUserDto != null;
        if (curUserDto.getId() != 0) {
            model.addAttribute("viewedBooks",
                    getConvertorService().convertEntityToDto(
                            getBookService()
                                    .getPageOfUserViewedBooks(curUserDto.getId(), 0, 20)
                                    .getContent(),
                            bookIds,
                            curUserDto.getId()));
        }

        return"books/viewed";
    }

    @GetMapping("/tag/{nameDto}")
    @ResponseBody
    public BooksPageDto getNextTagsPage(@RequestParam("offset") Integer offset,
                                        @RequestParam("limit") Integer limit,
                                        @PathVariable(value = "nameDto", required = false) TagNameDto nameDto,
                                        @CookieValue(value = "cartContents", required = false) String cartContents,
                                        @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                        Model model) {
        Page<BookEntity> page = getBookService().getPageOfBooksByTagName(nameDto.getName(), offset, limit);
        BooksPageDto booksPageDto = new BooksPageDto(getConvertorService().convertEntityToDto(page.getContent(),
                getBookIds(cartContents, postponedContents, model),
                getCurrentUserId()));
        booksPageDto.setCount((int) page.getTotalElements());
        return booksPageDto;
    }

    @GetMapping("/author/{slug}")
    public String slugPage(@PathVariable("slug") String slug,
                           @CookieValue(value = "cartContents", required = false) String cartContents,
                           @CookieValue(value = "postponedContents", required = false) String postponedContents,
                           Model model) {
        AuthorEntity authorEntity = authorService.getAuthorEntityBySlug(slug);
        model.addAttribute("authorDto", getConvertorService().convertEntityToDto(authorEntity));
        model.addAttribute("authorBooks", getConvertorService().convertEntityToDto(
                getBookService()
                        .getPageOfBooksByAuthorId(authorEntity.getId(), 0, 20)
                        .getContent(),
                getBookIds(cartContents, postponedContents, model),
                getCurrentUserId()));
        return "/books/author";
    }

    @GetMapping("/{slug}")
    public String bookSlugPage(@PathVariable("slug") String slug,
                               @CookieValue(value = "cartContents", required = false) String cartContents,
                               @CookieValue(value = "postponedContents", required = false) String postponedContents,
                               Model model) {
        int curUserId;
        if (model.getAttribute("curUser") instanceof UserDto curUserDto) {
            curUserId = curUserDto.getId();
        } else {
            curUserId = 0;
        }
        addModelAttributesForBookSlug(slug, model, curUserId);
        if (curUserId == 0) {
            addBooksCountModeleAttributeFromCookieContents(cartContents, postponedContents, model);
            return "/books/slug";
        } else {
            addBooksCountModeleAttributeFromDatabase(curUserId, model);
            return "/books/slugmy";
        }
    }

    private void addModelAttributesForBookSlug(String slug, Model model, Integer currentUserId) {
        BookEntity bookEntity = getBookService().getBookEntityBySlug(slug);
        model.addAttribute("slugBookDto", getConvertorService().convertBookEntityToSlugDto(bookEntity));
        model.addAttribute("bookDescription", bookEntity.getDescription());
        model.addAttribute("ratingsCount", bookEntity.getBookRatingEntityList().size());
        model.addAttribute("ratingDistributionDto", new RatingDistributionDto(bookEntity));
        List<BookReviewEntity> reviewEntities = bookReviewService.getReviewEntitiesOnBook(bookEntity);
        model.addAttribute("reviews", getConvertorService().convertReviewEntityListToDto(reviewEntities));
        model.addAttribute("reviewsCount", reviewEntities.size());
        model.addAttribute("userRatingValue", (currentUserId != 0 ?
                booksRatingAndPopularityService.getUserRating(bookEntity, currentUserId) :
                0));
        if (currentUserId != 0) {
            getBookService().saveLastViewTime(currentUserId, bookEntity.getId());
        }
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable("hash") String hash) throws IOException{
        Path path = storage.getBookFilePath(hash);
        Logger.getLogger(this.getClass().getName()).info("book file path: " + path);

        MediaType mediaType = storage.getBookFileMime(path);
        Logger.getLogger(this.getClass().getName()).info("book file mime type: " + mediaType);

        byte[] data = storage.getBookFileByteArray(path);
        Logger.getLogger(this.getClass().getName()).info("book data len: " + data.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }
}
