package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.data.dto.DocumentDto;
import com.example.mybookshopapp.data.services.BookService;
import com.example.mybookshopapp.data.services.DocumentsService;
import com.example.mybookshopapp.data.services.TransactionsService;
import com.example.mybookshopapp.data.services.convertors.Convertor;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.services.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author karl
 */

@Controller
@RequestMapping("/documents")
public class DocumentsController extends FatherController{

    private final DocumentsService documentsService;

    @Autowired
    public DocumentsController(BookstoreUserRegister userRegister,
                               CookieHandlerService cookieHandler,
                               JWTBlacklistService blacklistService,
                               Convertor convertorService,
                               BookService bookService,
                               TransactionsService transactionsService, DocumentsService documentsService) {
        super(userRegister, cookieHandler, blacklistService, convertorService, bookService, transactionsService);
        this.documentsService = documentsService;
    }

    @ModelAttribute("documentList")
    private List<DocumentDto> documentEntities() {
        return documentsService.getAllSortedDocuments();
    }

    @GetMapping("")
    public String documents(@CookieValue(value = "cartContents", required = false) String cartContents,
                            @CookieValue(value = "postponedContents", required = false) String postponedContents,
                            Model model) {
        getBookIds(cartContents, postponedContents, model);
        return "documents/index";
    }

    @GetMapping("/{slug}")
    public String handleSlug(@PathVariable("slug") String slug,
                             @CookieValue(value = "cartContents", required = false) String cartContents,
                             @CookieValue(value = "postponedContents", required = false) String postponedContents,
                             Model model) {
        model.addAttribute("document", documentsService.getDocumentEntityBySlug(slug));
        getBookIds(cartContents, postponedContents, model);
        return "documents/slug";
    }
}
