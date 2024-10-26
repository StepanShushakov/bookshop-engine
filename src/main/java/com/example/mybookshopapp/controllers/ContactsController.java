package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.dto.requests.MessageRequestDto;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.MessageService;
import com.example.mybookshopapp.data.services.TransactionsService;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author karl
 */

@Controller
@RequestMapping("/contacts")
public class ContactsController extends FatherController{

    private final MessageService messageService;

    @Autowired
    public ContactsController(BookstoreUserRegister userRegister, CookieHandlerService cookieHandler, JWTBlacklistService blacklistService, Convertor convertorService, BookService bookService, TransactionsService transactionsService, MessageService messageService) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
        this.messageService = messageService;
    }

    @GetMapping("")
    public String contacts(@CookieValue(value = "cartContents", required = false) String cartContents,
                           @CookieValue(value = "postponedContents", required = false) String postponedContents,
                           Model model) {
        getBookIds(cartContents, postponedContents, model);
        return "contacts";
    }

    @PostMapping("/sendMessage")
    public String sendMessage(@ModelAttribute MessageRequestDto messageRequest, RedirectAttributes redirectAttributes) {
        messageService.addMessage(messageRequest, getUserRegister().getCurrentUser());
        redirectAttributes.addFlashAttribute("sendMessageResult", true);
        return "redirect:/contacts";
    }
}
