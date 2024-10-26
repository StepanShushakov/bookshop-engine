package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.model.other.FaqEntity;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.HelpService;
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
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author karl
 */

@Controller
@RequestMapping("/faq")
public class HelpController extends FatherController{

    private final HelpService helpService;

    @Autowired
    public HelpController(BookstoreUserRegister userRegister, CookieHandlerService cookieHandler, JWTBlacklistService blacklistService, Convertor convertorService, BookService bookService, TransactionsService transactionsService, HelpService helpService) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
        this.helpService = helpService;
    }

    @ModelAttribute("faqList")
    public List<FaqEntity> faqEntities() {
        return helpService.getAllSortedFaqs();
    }

    @GetMapping("")
    public String faq(@CookieValue(value = "cartContents", required = false) String cartContents,
                      @CookieValue(value = "postponedContents", required = false) String postponedContents,
                      Model model) {
        getBookIds(cartContents, postponedContents, model);
        return "faq";
    }
}
