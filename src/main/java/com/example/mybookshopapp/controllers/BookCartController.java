package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.dto.BookDto;
import com.example.mybookshopapp.data.dto.UserDto;
import com.example.mybookshopapp.data.dto.cookie.BookIds;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.PaymentService;
import com.example.mybookshopapp.data.services.TransactionsService;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author karl
 */

@Controller
@RequestMapping("/books")
public class BookCartController extends FatherController{

    private final PaymentService paymentService;

    @Autowired
    public BookCartController(BookstoreUserRegister userRegister,
                              CookieHandlerService cookieHandler,
                              JWTBlacklistService blacklistService,
                              Convertor convertorService,
                              BookService bookService,
                              TransactionsService transactionsService,
                              PaymentService paymentService) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
        this.paymentService = paymentService;
    }

    @GetMapping("/pay")
    public RedirectView handlePay(Model model) throws NoSuchAlgorithmException {
        UserDto currentUser = (UserDto) model.getAttribute("curUser");
        assert currentUser != null;
        if (currentUser.getName() == null) {
            return new RedirectView("/signin");
        } else {
            List<BookDto> booksForPaid = getConvertorService().convertEntityToDto(
                    getBookService().getBookEntitiesByUserIdAndType(currentUser.getId(), "CART"),
                    new BookIds(),
                    currentUser.getId());
            StringJoiner sj = new StringJoiner("/");
            for (BookDto bookDto: booksForPaid) {
                sj.add(bookDto.getId().toString());
            }
            String paymentUrl = paymentService.getPaymentUrl(currentUser, "cart:"+ sj, booksForPaid);
            return new RedirectView(paymentUrl);
        }
    }
}
