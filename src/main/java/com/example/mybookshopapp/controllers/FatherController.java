package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.dto.BookDto;
import com.example.mybookshopapp.data.dto.BookSlugDto;
import com.example.mybookshopapp.data.dto.UserDto;
import com.example.mybookshopapp.data.dto.cookie.BookIds;
import com.example.mybookshopapp.data.model.user.UserEntity;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.TransactionsService;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.errs.BlacklistTokenException;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import lombok.Getter;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author karl
 */

@Getter
public class FatherController {

    private final BookstoreUserRegister userRegister;
    private final CookieHandlerService cookieHandler;
    private final JWTBlacklistService blacklistService;
    private final Convertor convertorService;
    private final BookService bookService;
    private final TransactionsService transactionsService;

    public FatherController(BookstoreUserRegister userRegister, CookieHandlerService cookieHandler, JWTBlacklistService blacklistService, Convertor convertorService, BookService bookService, TransactionsService transactionsService) {
        this.userRegister = userRegister;
        this.cookieHandler = cookieHandler;
        this.blacklistService = blacklistService;
        this.convertorService = convertorService;
        this.bookService = bookService;
        this.transactionsService = transactionsService;
    }

    @ModelAttribute("curUser")
    public UserDto currentUser(@CookieValue(value = "token", required = false) String token, Model model) throws BlacklistTokenException {
        if (blacklistService.tokenAtBlacklist(token)) {
            throw new BlacklistTokenException("using not valid token");
        }
        UserDto userDto = userRegister.getCurrentUserDto();
        if (userDto.getName() != null) {
            model.addAttribute("myBooksCount", 0);
            model.addAttribute("myCartCount", 0);
            model.addAttribute("myPostponedCount", 0);
        }
        return userDto;
    }


    public BookIds getBookIds(String cartContents, String postponedContents, Model model) {
        BookIds bookIds = new BookIds();
        int curUserID = getCurrentUserId();
        if (curUserID == 0) {
            bookIds.setCartIds(cookieHandler.getBookIdsFromContents(cartContents));
            bookIds.setPostponedIds(cookieHandler.getBookIdsFromContents(postponedContents));
            addBooksCountModeleAttributeFromCookieContents(bookIds, cartContents, postponedContents, model);
        } else {
            bookIds.setCartIds(bookService.getBookIdsBuUserIdAndTypeCode(curUserID, "CART"));
            bookIds.setPostponedIds(bookService.getBookIdsBuUserIdAndTypeCode(curUserID, "KEPT"));
            addBooksCountModeleAttributeFromDatabase(curUserID, model);
        }
        return bookIds;
    }

    public void addBooksCountModeleAttributeFromCookieContents(BookIds slugs, String cartContents, String postponedContents, Model model) {
        model.addAttribute("cartCount", cartContents == null || cartContents.isEmpty() ? 0 : slugs.getCartIds().size());
        model.addAttribute("postponedCount", postponedContents == null || postponedContents.isEmpty() ? 0 : slugs.getPostponedIds().size());
    }

    public void addBooksCountModeleAttributeFromCookieContents(String cartContents, String postponedContents, Model model) {
        BookIds bookIds = new BookIds();
        bookIds.setCartIds(cookieHandler.getBookIdsFromContents(cartContents));
        bookIds.setPostponedIds(cookieHandler.getBookIdsFromContents(postponedContents));
        model.addAttribute("cartCount", cartContents == null || cartContents.isEmpty() ? 0 : bookIds.getCartIds().size());
        model.addAttribute("postponedCount", postponedContents == null || postponedContents.isEmpty() ? 0 : bookIds.getPostponedIds().size());
    }

    public void addBooksCountModeleAttributeFromDatabase(int userId, Model model) {
        model.addAttribute("cartCount", cookieHandler.getBookCountFromDataBase(userId, new String[]{"CART"}));
        model.addAttribute("postponedCount", cookieHandler.getBookCountFromDataBase(userId, new String[]{"KEPT"}));
        model.addAttribute("myCount", cookieHandler.getBookCountFromDataBase(userId, new String[]{"PAID", "ARCHIVED"}));
        model.addAttribute("balanceSum", transactionsService.getBalanceOnUserId(userId));
    }

    public void addBooksFromCookieToModelAttribute(List<Integer> cookieIds, String attributeName, String emptyAttributeName, Model model) {
        if (cookieIds == null) {
            model.addAttribute(emptyAttributeName, true);
        } else {
            model.addAttribute(emptyAttributeName, false);
            List<BookSlugDto> booksFromCookieSlugs = convertorService.convertBookEntityToSlugDto(bookService.getBookEntitiesByIdIn(cookieIds));
            model.addAttribute(attributeName, booksFromCookieSlugs);
        }
    }

    public void addBooksFromDataBaseToModelAttribute(int curUserId, String type, String attributeName, String emptyAttributeName, Model model) {
        addBooksCountModeleAttributeFromDatabase(curUserId, model);
        List<BookSlugDto> booksFromDatabase = convertorService.convertBookEntityToSlugDto(bookService.getBookEntitiesByUserIdAndType(curUserId, type));
        if (booksFromDatabase.isEmpty()) {
            model.addAttribute(emptyAttributeName, true);
        } else {
            model.addAttribute(emptyAttributeName, false);
            model.addAttribute(attributeName, booksFromDatabase);
            if (type.equals("KEPT")) {
                StringJoiner sj = new StringJoiner(", ");
                booksFromDatabase.forEach(b -> sj.add(b.getId().toString()));
                model.addAttribute("allPostponedIds", "[" +sj + "]");
            }
        }
    }

    public void addTotalSumAttribute(String fromAttributeName, Model model) {
        int totalPriceOld = 0;
        int totalPrice = 0;
        if (model.getAttribute(fromAttributeName) instanceof List<?> bookList) {
            for (Object book: bookList) {
                BookDto bookDto = ((BookDto) book);
                totalPriceOld = totalPriceOld + bookDto.getPrice();
                totalPrice = totalPrice + (bookDto.getDiscount() > 0 ? bookDto.getDiscountPrice() : bookDto.getPrice());
            }
        }
        model.addAttribute("totalPriceOld", totalPriceOld);
        model.addAttribute("totalPrice", totalPrice);

    }

    public int getCurrentUserId() {
        Object curUsr = userRegister.getCurrentUser();
        int userId = 0;
        if (curUsr instanceof UserEntity curUsrEnt)  {
            userId = curUsrEnt.getId();
        }
        return userId;
    }
}
