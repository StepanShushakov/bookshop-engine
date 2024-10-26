package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.dto.BookDto;
import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.model.book.links.Book2UserEntity;
import com.example.mybookshopapp.data.model.payments.BalanceTransactionEntity;
import com.example.mybookshopapp.data.repositories.BalanceTransactionRepository;
import com.example.mybookshopapp.data.repositories.Book2UserTypeEntityRepository;
import com.example.mybookshopapp.data.model.enums.InvoiceState;
import com.example.mybookshopapp.data.model.payments.InvoiceEntity;
import com.example.mybookshopapp.data.repositories.Book2UserRepository;
import com.example.mybookshopapp.data.repositories.InvoiceRepository;
import com.example.mybookshopapp.data.services.supportingservice.CookieHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author karl
 */

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CookieHandlerService cookieHandler;
    private final BookService bookService;
    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeEntityRepository book2UserTypeEntityRepository;
    private final BalanceTransactionRepository balanceTransactionRepository;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, CookieHandlerService cookieHandler, BookService bookService, Book2UserRepository book2UserRepository,
                          Book2UserTypeEntityRepository book2UserTypeEntityRepository, BalanceTransactionRepository balanceTransactionRepository) {
        this.invoiceRepository = invoiceRepository;
        this.cookieHandler = cookieHandler;
        this.bookService = bookService;
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeEntityRepository = book2UserTypeEntityRepository;
        this.balanceTransactionRepository = balanceTransactionRepository;
    }

    public void buyAllInvoice(String invId) {
        InvoiceEntity invoice = invoiceRepository.findInvoiceEntitiesById(Integer.valueOf(invId));
        if (invoice != null && invoice.getContents().contains("cart:")) {
            invoice.setState(InvoiceState.PAID);
            invoiceRepository.save(invoice);
            List<Integer> cookieIds = cookieHandler.getBookIdsFromContents(invoice.getContents().replaceAll("cart:", ""));
            List<BookEntity> booksFromCookieSlugs = bookService.getBookEntitiesByIdIn(cookieIds);
            for (BookEntity bookEntity: booksFromCookieSlugs) {
                Book2UserEntity book2User = book2UserRepository.findBook2UserEntitiesByBookIdAndUserId(bookEntity.getId(), invoice.getUser().getId());
                if (book2User == null) {
                    book2User = new Book2UserEntity();
                    book2User.setBookId(bookEntity.getId());
                    book2User.setUserId(invoice.getUser().getId());
                    book2User.setTime(LocalDateTime.now());
                }
                book2User.setTypeId(book2UserTypeEntityRepository.findByCode("PAID").getId());
                book2UserRepository.save(book2User);
                BookDto bookDto = new BookDto(bookEntity);
                BalanceTransactionEntity balanceTransaction = new BalanceTransactionEntity();
                balanceTransaction.setUser(invoice.getUser());
                balanceTransaction.setBook(bookEntity);
                balanceTransaction.setValue(-bookDto.getDiscountPrice());
                balanceTransaction.setTime(LocalDateTime.now());
                balanceTransaction.setDescription("Покупка книги&#32;<a href=\"/books/"+bookDto.getSlug()+"\">"+bookDto.getTitle()+"</a>");
                balanceTransactionRepository.save(balanceTransaction);
            }
        }
    }
}
