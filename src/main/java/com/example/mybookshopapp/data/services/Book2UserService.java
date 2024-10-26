package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.model.book.links.Book2UserEntity;
import com.example.mybookshopapp.data.model.book.links.Book2UserTypeEntity;
import com.example.mybookshopapp.data.repositories.Book2UserRepository;
import com.example.mybookshopapp.data.repositories.Book2UserTypeEntityRepository;
import com.example.mybookshopapp.data.repositories.BookRepository;
import com.example.mybookshopapp.errs.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author karl
 */

@Service
public class Book2UserService {

    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeEntityRepository book2UserTypeEntityRepository;
    private final BookRepository bookRepository;

    @Autowired
    public Book2UserService(Book2UserRepository book2UserRepository,
                            Book2UserTypeEntityRepository book2UserTypeEntityRepository,
                            BookRepository bookRepository) {
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeEntityRepository = book2UserTypeEntityRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void removeBook2UserOnUserIdAndBookSlug(int userId, int bookId){
        book2UserRepository.deleteBook2UserEntityByUserIdAndBookSlug(userId, bookId);
    }

    public void addBook2UserLink(int userId, Integer bookId, String typeCode, LocalDateTime time) {
        Book2UserEntity book2UserEntity = new Book2UserEntity();
        book2UserEntity.setUserId(userId);
        book2UserEntity.setBookId(bookId);
        book2UserEntity.setTypeId(book2UserTypeEntityRepository.findByCode(typeCode).getId());
        book2UserEntity.setTime(time);
        book2UserRepository.save(book2UserEntity);
    }

    public void changeUserBookStatus(int userId, int bookId, String status) throws BadRequestException {
        BookEntity bookEntity = (BookEntity) bookRepository.findBookEntityById(bookId);
        if (bookEntity == null) {
            throw new BadRequestException("no such book id");
        }
        Book2UserEntity book2UserEntity = book2UserRepository.findBook2UserEntitiesByBookIdAndUserId(bookId, userId);
        if (book2UserEntity == null && (status.equals("ARCHIVED") || status.equals("PAID"))) {
            throw new BadRequestException("book must be buyed");
        }
        if (book2UserEntity == null) {
            book2UserEntity = new Book2UserEntity();
            book2UserEntity.setUserId(userId);
            book2UserEntity.setBookId(bookEntity.getId());
        }
        Optional<Book2UserTypeEntity> book2UserTypeEntity = book2UserTypeEntityRepository.findById(book2UserEntity.getTypeId());
        if (book2UserTypeEntity.isPresent()) {
            String oldStatus = book2UserTypeEntity.get().getCode();
            if (status.equals("ARCHIVED")
                    && !(oldStatus.equals("PAID") || oldStatus.equals("ARCHIVED"))) {
                throw new BadRequestException("unread book don't move to archive");
            } else if (status.equals("PAID")
                    && !(oldStatus.equals("PAID") || oldStatus.equals("ARCHIVED"))) {
                throw new BadRequestException("not archived book don't must be unread");
            }
            if ((status.equals("KEPT") || status.equals("CART"))
                    && (oldStatus.equals("PAID") || oldStatus.equals("ARCHIVED"))) {
                throw new BadRequestException("book already buyed");
            }
        }
        book2UserEntity.setTime(LocalDateTime.now());
        Book2UserTypeEntity newBook2UserTypeEntity = book2UserTypeEntityRepository.findByCode(status);
        book2UserEntity.setTypeId(newBook2UserTypeEntity.getId());
        book2UserRepository.save(book2UserEntity);
    }
}
