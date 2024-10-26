package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.ApiResponse;
import com.example.mybookshopapp.data.ApiTypicalResponse;
import com.example.mybookshopapp.data.dto.BookDto;
import com.example.mybookshopapp.data.dto.BooksPageDto;
import com.example.mybookshopapp.data.dto.TransactionPageDto;
import com.example.mybookshopapp.data.dto.UserDto;
import com.example.mybookshopapp.data.dto.requests.BookReviewRequestDto;
import com.example.mybookshopapp.data.dto.requests.ChangeStatusRequestDto;
import com.example.mybookshopapp.data.dto.requests.RateBookRequestDto;
import com.example.mybookshopapp.data.dto.requests.RateBookReviewRequestDto;
import com.example.mybookshopapp.data.model.user.UserEntity;
import com.example.mybookshopapp.data.services.Book2UserService;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.BooksRatingAndPopularityService;
import com.example.mybookshopapp.data.services.TransactionsService;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.errs.BadRequestException;
import com.example.mybookshopapp.errs.BookstoreApiWrongParameterException;
import com.example.mybookshopapp.errs.UserNotPermissionException;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author karl
 */

@RestController
@RequestMapping("/api")
@Api("book data api")
public class BooksRestApiController extends FatherController{

    private final MessageSource messageSource;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final BookstoreUserRegister userRegister;
    private final Book2UserService book2UserService;

    @Autowired
    public BooksRestApiController(BookstoreUserRegister userRegister,
                                  CookieHandlerService cookieHandler,
                                  JWTBlacklistService blacklistService,
                                  Convertor convertorService,
                                  BookService bookService,
                                  TransactionsService transactionsService,
                                  MessageSource messageSource,
                                  BooksRatingAndPopularityService booksRatingAndPopularityService,
                                  Book2UserService book2UserService) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
        this.messageSource = messageSource;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.userRegister = userRegister;
        this.book2UserService = book2UserService;
    }

    @GetMapping("/books/by-author")
    @ApiOperation("operation to get book list of bookshop by passed author name")
    public ResponseEntity<List<BookDto>> booksByAuthor(@RequestParam("author") String authorName,
                                                       @CookieValue(value = "cartContents", required = false) String cartContents,
                                                       @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                                       Model model) {
        return  ResponseEntity.ok(getConvertorService().convertEntityToDto(
                    getBookService().getBooksEntitiesByAuthors(authorName),
                    getBookIds(cartContents, postponedContents, model),
                    getCurrentUserId()));

    }

    @GetMapping("/books/by-title")
    @ApiOperation("get books by title")
    public ResponseEntity<ApiResponse<BookDto>> booksByTitle(@RequestParam("title") String title,
                                                             @CookieValue(value = "cartContents", required = false) String cartContents,
                                                             @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                                             Model model) throws BookstoreApiWrongParameterException {
        ApiResponse<BookDto> response = new ApiResponse<>();
        List<BookDto> data = getConvertorService().convertEntityToDto(
                getBookService().getBookEntitiesByTitle(title),
                getBookIds(cartContents, postponedContents, model),
                getCurrentUserId());
        response.setDebugMessage("successful request");
        response.setMessage("data size: " + data.size() + " elements");
        response.setStatus(HttpStatus.OK);
        response.setTimeStamp(LocalDateTime.now());
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/books/by-price-range")
    @ApiOperation("get book by price range from min price to max price")
    public ResponseEntity<List<BookDto>> priceRangeBooks(@RequestParam("min") Integer min,
                                                         @RequestParam("max") Integer max,
                                                         @CookieValue(value = "cartContents", required = false) String cartContents,
                                                         @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                                         Model model) {
        return ResponseEntity.ok(getConvertorService().convertEntityToDto(
                    getBookService().getBookEntitiesWithPriceBetween(min, max),
                    getBookIds(cartContents, postponedContents, model),
                    getCurrentUserId()));
    }

    @GetMapping("/books/with-max-discount")
    @ApiOperation("get list of book with max discount")
    public ResponseEntity<List<BookDto>> maxDiscountBooks(@CookieValue(value = "cartContents", required = false) String cartContents,
                                                          @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                                          Model model) {
        return ResponseEntity.ok(getConvertorService().convertEntityToDto(
                    getBookService().getBookEntitiesWithMaxDiscount(),
                    getBookIds(cartContents, postponedContents, model),
                    getCurrentUserId()));
    }

    @GetMapping("/books/bestsellers")
    @ApiOperation("get bestseller books (wits us_bestseller = 1)")
    public ResponseEntity<List<BookDto>> bestsellerBooks(@CookieValue(value = "cartContents", required = false) String cartContents,
                                                         @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                                         Model model) {
        return ResponseEntity.ok(getConvertorService().convertEntityToDto(
                    getBookService().getBestsellers(),
                    getBookIds(cartContents, postponedContents, model),
                    getCurrentUserId()));
    }

    @GetMapping("/books/by-price")
    public ResponseEntity<List<BookDto>> booksByPrice(@RequestParam("price") Integer price,
                                                      @CookieValue(value = "cartContents", required = false) String cartContents,
                                                      @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                                      Model model) {
        return ResponseEntity.ok(getConvertorService().convertEntityToDto(
                getBookService().getBookEntitiesByPrice(price),
                getBookIds(cartContents, postponedContents, model),
                getCurrentUserId()));
    }

    @GetMapping("/books/recent")
    @ResponseBody
    public BooksPageDto getNextRecentBookPage(@RequestParam("from") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate from,
                                              @RequestParam("to") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate to,
                                              @RequestParam("offset") Integer offset,
                                              @RequestParam("limit") Integer limit,
                                              @CookieValue(name = "cartContents", required = false) String cartContents,
                                              @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                              Model model) {
        return getConvertorService()
                .convertBooksPageToBooksPageDto(getBookService()
                        .getPageOfRecentBooks(from, to, offset, limit),
                        getBookIds(cartContents, postponedContents, model),
                        getCurrentUserId());
    }

    @GetMapping("/books/popular")
    @ResponseBody
    public BooksPageDto getNextPopularBookPage(@RequestParam("offset") Integer offset,
                                               @RequestParam("limit") Integer limit,
                                               @CookieValue(name = "cartContents", required = false) String cartContents,
                                               @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                               Model model) {
        return getConvertorService()
                .convertBooksPageToBooksPageDto(booksRatingAndPopularityService
                        .getPageOfPopularBooks(offset, limit),
                        getBookIds(cartContents, postponedContents, model),
                        getCurrentUserId());
    }

    @GetMapping("/books/viewed")
    @ResponseBody
    public BooksPageDto getNextViewedBookPage(@RequestParam("offset") Integer offset,
                                              @RequestParam("limit") Integer limit,
                                              @CookieValue(name = "cartContents", required = false) String cartContents,
                                              @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                              Model model) {
        int curUserId = getCurrentUserId();
        return getConvertorService()
                .convertBooksPageToBooksPageDto(getBookService().getPageOfUserViewedBooks(curUserId, offset, limit),
                        getBookIds(cartContents, postponedContents, model),
                        curUserId);
    }

    @GetMapping("/books/tags-by-book_id")
    @ResponseBody
    public List<String> getBookTags(@RequestParam("book_id") Integer bookId) {
        return getBookService().getTagsOnBookId(bookId);
    }

    @GetMapping("/books/genre/{slug}")
    @ResponseBody
    public BooksPageDto getNextGenrePage(@RequestParam("offset") Integer offset,
                                         @RequestParam("limit") Integer limit,
                                         @PathVariable(value = "slug", required = false) String slug,
                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                         Model model) {
        return getConvertorService()
                .convertBooksPageToBooksPageDto(getBookService().getPageOfGenreSlug(slug, offset, limit),
                        getBookIds(cartContents, postponedContents, model),
                        getCurrentUserId());
    }

    @GetMapping("/books/author/{ID}")
    @ResponseBody
    @ApiOperation("get books page by author id, order by descending date of publication")
    public BooksPageDto getNextAuthorBooksPage(@RequestParam("offset") Integer offset,
                                               @RequestParam("limit") Integer limit,
                                               @PathVariable("ID") Integer authorId,
                                               @CookieValue(name = "cartContents", required = false) String cartContents,
                                               @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                               Model model) {
        return getConvertorService()
                .convertBooksPageToBooksPageDto(getBookService().getPageOfBooksByAuthorId(authorId, offset, limit),
                        getBookIds(cartContents, postponedContents, model),
                        getCurrentUserId());
    }

    @GetMapping("/transactions")
    @ResponseBody
    public TransactionPageDto getCurrentUserTransactions(@RequestParam("offset") Integer offset,
                                                         @RequestParam("limit") Integer limit,
                                                         @RequestParam("sort") String sort) {
        return getConvertorService()
                .convertTransactionsPageToTransactionsPageDto(getTransactionsService()
                        .getPageOfUserTransactions((UserEntity) userRegister.getCurrentUser(), sort, offset, limit));
    }


    @PostMapping("/changeBookStatus")
    public ResponseEntity<ApiTypicalResponse> handleChangeBookStatus(@RequestBody ChangeStatusRequestDto requestParams,
                                                              @CookieValue(name = "cartContents", required = false) String cartContents,
                                                              @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                                              HttpServletResponse response,
                                                              Model model) throws BadRequestException {
        int curUserId;
        if (model.getAttribute("curUser") instanceof UserDto curUserDto) {
            curUserId = curUserDto.getId();
        } else {
            curUserId = 0;
        }
        changeBookStatus(curUserId, requestParams.getBooksIds(), requestParams.getStatus(), cartContents, postponedContents, response, model);

        return new ResponseEntity<>(new ApiTypicalResponse(), HttpStatus.OK);
    }

    private void changeBookStatus(int curUserId, List<Integer> bookIds, String status, String cartContents, String postponedContents, HttpServletResponse response, Model model) throws BadRequestException {
        for (int bookId: bookIds) {
            if (curUserId != 0) {
                if (status.equals("UNLINK")) {
                    removeFromBook2User(curUserId, bookId);
                } else {
                    changeUserBookStatus(curUserId, bookId, status);
                }
            } else {
                switch (status) {
                    case "UNLINK" -> {
                        cartContents = removeFromContents(bookId, cartContents, "cart", "isCartEmpty", response, model);
                        postponedContents = removeFromContents(bookId, postponedContents, "postponed", "isPostponedEmpty", response, model);
                    }
                    case "KEPT" -> {
                        cartContents = removeFromContents(bookId, cartContents, "cart", "isCartEmpty", response, model);
                        postponedContents = addBookToContents(bookId, postponedContents, "postponed", "isPostponedEmpty", response, model);
                    }
                    case "CART" -> {
                        postponedContents = removeFromContents(bookId, postponedContents, "postponed", "isPostponedEmpty", response, model);
                        cartContents = addBookToContents(bookId, cartContents, "cart", "isCartEmpty", response, model);
                    }
                    case "ARCHIVED" -> throw new BadRequestException("unauthorized users cannot archive books");
                    case "PAID" -> throw new BadRequestException("unauthorized users cannot set paid status");
                }
            }
        }
    }

    private void changeUserBookStatus(int curUserId, int bookId, String status) throws BadRequestException {
        book2UserService.changeUserBookStatus(curUserId, bookId, status);
    }

    @PostMapping("/bookReview")
    public ResponseEntity<ApiTypicalResponse> handleBookReview(@RequestBody BookReviewRequestDto requestParams,
                                                        @CookieValue(name = "token", required = false) String token) throws UserNotPermissionException {
        userRegister.checkAuthorization(token);
        ApiTypicalResponse response = new ApiTypicalResponse();
        String text = requestParams.getText();
        if (text.length() <= 150) {
            response.setResult(false);
            response.setError(messageSource.getMessage("book.review.tooShort", null, LocaleContextHolder.getLocale()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        getBookService().addReview((UserEntity) userRegister.getCurrentUser(),
                requestParams.getBookId(),
                requestParams.getText());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/rateBook")
    public ResponseEntity<ApiTypicalResponse> handleRateBook(@RequestBody RateBookRequestDto requestParams,
                                                      @CookieValue(name = "token", required = false) String token) throws UserNotPermissionException {

        userRegister.checkAuthorization(token);
        booksRatingAndPopularityService.setBookRate((UserEntity) userRegister.getCurrentUser(), requestParams.getBookId(), requestParams.getValue());
        return new ResponseEntity<>(new ApiTypicalResponse(), HttpStatus.OK);
    }

    @PostMapping("/rateBookReview")
    public ResponseEntity<ApiTypicalResponse> handleRateBookReview(@RequestBody RateBookReviewRequestDto requestParams,
                                                            @CookieValue(name = "token", required = false) String token) throws UserNotPermissionException {

        userRegister.checkAuthorization(token);
        ApiTypicalResponse response = new ApiTypicalResponse();
        if (requestParams.getValue() == -1 || requestParams.getValue() == 1) {
            booksRatingAndPopularityService.setReviewLike(((UserEntity) userRegister.getCurrentUser()),
                    requestParams.getReviewId(), requestParams.getValue());
            response.setResult(true);
        } else {
            response.setResult(false);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private boolean contentsIsEmpty(String contents) {
        return (contents == null || contents.isEmpty());
    }

    private String addBookToContents(int bookId,
                                   String contents,
                                   String cookieName,
                                   String attributeName,
                                   HttpServletResponse response,
                                   Model model) {
        String stringBookId = "-" + bookId + "-";
        if(contentsIsEmpty(contents)) {
            Cookie cookie = new Cookie(cookieName + "Contents", stringBookId);
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute(attributeName, false);
            return stringBookId;
        } else if(!contents.contains(stringBookId)) {
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(contents).add(stringBookId);
            String returnedContents = stringJoiner.toString();
            Cookie cookie = new Cookie(cookieName + "Contents", returnedContents);
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute(attributeName, false);
            return returnedContents;
        }
        return "";
    }

    private String removeFromContents(int bookId,
                                    String contents,
                                    String cookieName,
                                    String attributeName,
                                    HttpServletResponse response,
                                    Model model) {
        if (!contentsIsEmpty(contents)) {
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(contents.split("/")));
            cookieBooks.remove("-" + bookId + "-");
            Cookie cookie = new Cookie(cookieName + "Contents", String.join("/", cookieBooks));
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute(attributeName, cookieBooks.isEmpty());
            StringJoiner sj = new StringJoiner("/");
            for (String cookieBook: cookieBooks) {
                sj.add(cookieBook);
            }
            return sj.toString();
        } else {
            model.addAttribute(attributeName, true);
            return "";
        }
    }

    private void removeFromBook2User(int userId, int bookId) {
        book2UserService.removeBook2UserOnUserIdAndBookSlug(userId, bookId);
    }

    @PostMapping("/changePubDate")
    public ResponseEntity<ApiTypicalResponse> changePubDate(@RequestBody String body) {
        getBookService().addDaysToPubDate(Integer.parseInt(body));
        return new ResponseEntity<>(new ApiTypicalResponse(), HttpStatus.OK);
    }
}
