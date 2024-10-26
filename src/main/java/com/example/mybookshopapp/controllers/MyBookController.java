package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.dto.BookDto;
import com.example.mybookshopapp.data.dto.cookie.BookIds;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.TransactionsService;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * @author karl
 */

@Controller
public class MyBookController extends FatherController{

    @Autowired
    public MyBookController(BookstoreUserRegister userRegister,
                            CookieHandlerService cookieHandler,
                            JWTBlacklistService blacklistService,
                            Convertor convertorService,
                            BookService bookService,
                            TransactionsService transactionsService) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
    }

    @ModelAttribute("myUnreadBooks")
    public List<BookDto> myUnreadBooks(@CookieValue(value = "cartContents", required = false) String cartContents,
                                       @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                       Model model) {
        int curUserId = getCurrentUserId();
        return getConvertorService().convertEntityToDto(getBookService()
                .getBookEntitiesByUserIdAndType(curUserId, "PAID"),
                getBookIds(cartContents, postponedContents, model),
                curUserId);
    }

    @ModelAttribute("myArchiveBooks")
    public List<BookDto> myArchiveBooks(@CookieValue(value = "cartContents", required = false) String cartContents,
                                        @CookieValue(value = "postponedContents", required = false) String postponedContents,
                                        Model model) {
        int curUserId = getCurrentUserId();
        return getConvertorService().convertEntityToDto(getBookService()
                .getBookEntitiesByUserIdAndType(curUserId, "ARCHIVED"),
                getBookIds(cartContents, postponedContents, model),
                curUserId);
    }

    @GetMapping("/my")
    public String handleMy(Model model) {
        addBooksCountModeleAttributeFromDatabase(getUserRegister().getCurrentUserDto().getId(), model);
        return "my";
    }

    @GetMapping("/myarchive")
    public String handeMyArchive(Model model) {
        addBooksCountModeleAttributeFromDatabase(getUserRegister().getCurrentUserDto().getId(), model);
        return "myarchive";
    }

    @GetMapping("/cart")
    public String cart(@CookieValue(value = "cartContents", required = false) String cartContents,
                       @CookieValue(value = "postponedContents", required = false) String postponedContents,
                       Model model) {
        int curUserId = getCurrentUserId();
        if (curUserId == 0) {
            BookIds bookIds = getBookIds(cartContents, postponedContents, model);
            addBooksFromCookieToModelAttribute(
                    bookIds.getCartIds(),
                    "bookCart",
                    "isCartEmpty",
                    model);
        } else {
            addBooksFromDataBaseToModelAttribute(
                    curUserId,
                    "CART",
                    "bookCart",
                    "isCartEmpty",
                    model);
        }
        addTotalSumAttribute("bookCart", model);
        return "cart";
    }

    @GetMapping("/postponed")
    public String postponed(@CookieValue(value = "cartContents", required = false) String cartContents,
                            @CookieValue(value = "postponedContents", required = false) String postponedContents,
                            Model model) {
        int curUserId = getCurrentUserId();
        if (curUserId == 0) {
            BookIds bookIds = getBookIds(cartContents, postponedContents, model);
            addBooksFromCookieToModelAttribute(
                    bookIds.getPostponedIds(),
                    "bookPostponed",
                    "isPostponedEmpty",
                    model);
            model.addAttribute("allPostponedIds", bookIds.getAllPostponedIds());
        } else {
            addBooksFromDataBaseToModelAttribute(
                    curUserId,
                    "KEPT",
                    "bookPostponed",
                    "isPostponedEmpty",
                    model);
        }
        return "postponed";
    }
}
