package com.example.mybookshopapp.data.services.convertors;

import com.example.mybookshopapp.data.dto.*;
import com.example.mybookshopapp.data.dto.cookie.BookIds;
import com.example.mybookshopapp.data.dto.requests.SaveBookDto;
import com.example.mybookshopapp.data.model.author.AuthorEntity;
import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.model.book.TagEntity;
import com.example.mybookshopapp.data.model.book.review.BookReviewEntity;
import com.example.mybookshopapp.data.model.genre.GenreEntity;
import com.example.mybookshopapp.data.model.payments.BalanceTransactionEntity;
import com.example.mybookshopapp.data.repositories.*;
import com.example.mybookshopapp.data.services.GenreComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author karl
 */

@Component
public class Convertor {

    private final MessageSource messageSource;
    private final BookReviewLikeRepository bookReviewLikeRepository;
    private final AuthorRepository authorRepository;
    private final BookTagRepository bookTagRepository;
    private final Book2UserRepository book2UserRepository;

    @Autowired
    public Convertor(MessageSource messageSource, BookReviewLikeRepository bookReviewLikeRepository, AuthorRepository authorRepository,
                     BookTagRepository bookTagRepository, Book2UserRepository book2UserRepository) {
        this.messageSource = messageSource;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
        this.authorRepository = authorRepository;
        this.bookTagRepository = bookTagRepository;
        this.book2UserRepository = book2UserRepository;
    }

    public BookSlugDto convertBookEntityToSlugDto(BookEntity bookEntity) {
        BookSlugDto bookSlugDto = new BookSlugDto(bookEntity);
        if (bookEntity.getId() == null) {
            return bookSlugDto;
        }
        bookSlugDto.setAutorList(
                convertAuthorListEntityToDto(authorRepository.findAuthorEntitiesByBook(bookEntity.getId())));
        bookSlugDto.setTagList(
                convertTagListEntityToDto(bookTagRepository.findTagsOnBookId(bookEntity.getId()))
        );
        setBookFiles(bookEntity, bookSlugDto);
        return bookSlugDto;
    }

    public BookDto convertEntityToDto(BookEntity bookEntity) {
        BookDto bookDto = new BookDto(bookEntity);
        if (bookEntity.getId() != null) {
            bookDto.setAuthors(authorRepository
                    .getAuthorsStringOnBookId(bookEntity.getId(),
                            messageSource.getMessage("book.author.hasMore",
                                    null,
                                    LocaleContextHolder.getLocale())));
        }
        return bookDto;
    }

    public BookDto convertEntityToDto(BookEntity bookEntity, BookIds bookIds, int userId) {
        BookDto bookDto = new BookDto(bookEntity);
        if (bookEntity.getId() != null) {
            bookDto.setAuthors(authorRepository
                    .getAuthorsStringOnBookId(bookEntity.getId(),
                            messageSource.getMessage("book.author.hasMore",
                                    null,
                                    LocaleContextHolder.getLocale())));
        }
        if (bookIds.getCartIds() != null && bookIds.getCartIds().contains(bookEntity.getId())) {
            bookDto.setStatus("CART");
        } else if (bookIds.getPostponedIds() != null && bookIds.getPostponedIds().contains(bookEntity.getId())) {
            bookDto.setStatus("KEPT");
        } else {
            String status = book2UserRepository.findCodeTypeByUserIdAndBookId(bookDto.getId(), userId);
            bookDto.setStatus(status != null && status.equals("ARCHIVED") ? "PAID" : status);
        }
        return bookDto;
    }

    private void setBookFiles(BookEntity bookEntity, BookSlugDto bookSlugDto) {
        List<BookFileDto> bookFileDtoList = new ArrayList<>();
        bookEntity.getBooksFileEntitiesList().forEach(e -> {
            BookFileDto d = new BookFileDto(e);
            d.setBookDto(bookSlugDto);
            d.setBookFileExtensionString(e.getType().getName());
            bookFileDtoList.add(d);
        });
        bookSlugDto.setBookFiles(bookFileDtoList);
    }

    public List<BookDto> convertEntityToDto(List<BookEntity> bookEntities) {
        List<BookDto> listDto = new ArrayList<>();
        bookEntities.forEach(entity -> listDto.add(convertEntityToDto(entity)));
        return listDto;
    }

    public List<BookSlugDto> convertBookEntityToSlugDto(List<BookEntity> bookEntities) {
        List<BookSlugDto> listDto = new ArrayList<>();
        bookEntities.forEach(entity -> listDto.add(convertBookEntityToSlugDto(entity)));
        return listDto;
    }

    public List<BookDto> convertEntityToDto(List<BookEntity> bookEntities, BookIds bookIds, int userId) {
        List<BookDto> listDto = new ArrayList<>();
        bookEntities.forEach(entity -> listDto.add(convertEntityToDto(entity, bookIds, userId)));
        return listDto;
    }

    public BookReviewDto convertEntityToDto(BookReviewEntity bookReviewEntity){
        BookReviewDto bookReviewDto = new BookReviewDto(bookReviewEntity);
        bookReviewDto.setLikeCount(bookReviewLikeRepository.getLikesCountOnReviewId(bookReviewEntity.getId()));
        bookReviewDto.setDislikeCount(bookReviewLikeRepository.getDislikesCountOnReviewId(bookReviewEntity.getId()));
        return bookReviewDto;
    }

    public List<BookReviewDto> convertReviewEntityListToDto(List<BookReviewEntity> bookReviewEntities) {
       List<BookReviewDto> listDto = new ArrayList<>();
       bookReviewEntities.forEach(entity -> listDto.add(convertEntityToDto(entity)));
       listDto.sort(new BookReviewDtoDescRatingComparator());
       return listDto;
    }

    private TransactionDto convertEntityToDto(BalanceTransactionEntity entity) {
        return new TransactionDto(entity);
    }

    public List<TransactionDto> convertBalanceTransactionEntityToDto(List<BalanceTransactionEntity> balanceTransactionEntities) {
        List<TransactionDto> listDto = new ArrayList<>();
        balanceTransactionEntities.forEach(entity -> listDto.add(convertEntityToDto(entity)));
        return listDto;
    }

    public AuthorDto convertEntityToDto(AuthorEntity authorEntity) {
        return new AuthorDto(authorEntity);
    }

    public TagNameDto convertEntityToDto(TagEntity tagEntity) {
        return new TagNameDto(tagEntity);
    }

    public List<AuthorDto> convertAuthorListEntityToDto(List<AuthorEntity> authorEntities) {
        List<AuthorDto> listDto = new ArrayList<>();
        authorEntities.forEach(author -> listDto.add(convertEntityToDto(author)));
        return listDto;
    }

    private List<TagNameDto> convertTagListEntityToDto(List<TagEntity> tagEntities) {
        List<TagNameDto> listDto = new ArrayList<>();
        tagEntities.forEach(tag -> listDto.add(convertEntityToDto(tag)));
        return listDto;
    }

    public static GenreDto convertGenreEntityToDto(GenreEntity genreEntity) {
        GenreDto genreDto = new GenreDto(genreEntity);
        List<GenreDto> childrenDto = new ArrayList<>();
        for (GenreEntity child: genreEntity.getChildren()) {
            childrenDto.add(convertGenreEntityToDto(child));
        }
        genreDto.setChildren(childrenDto.stream().sorted(new GenreComparator()).toList());
        return genreDto;
    }

    public BooksPageDto convertBooksPageToBooksPageDto(Page<BookEntity> page, BookIds bookIds, int userId) {
        BooksPageDto booksPageDto = new BooksPageDto(convertEntityToDto(page.getContent(), bookIds, userId));
        booksPageDto.setCount((int) page.getTotalElements());
        return booksPageDto;
    }

    public TransactionPageDto convertTransactionsPageToTransactionsPageDto(Page<BalanceTransactionEntity> page) {
        TransactionPageDto transactionPageDto = new TransactionPageDto(convertBalanceTransactionEntityToDto(page.getContent()));
        transactionPageDto.setCount(page.getTotalPages());
        return transactionPageDto;
    }

    public SaveBookDto convertBookEntityToSaveDto(BookEntity bookEntity) {
        SaveBookDto saveBookDto = new SaveBookDto(bookEntity);
        authorRepository.findAuthorEntitiesByBook(bookEntity.getId()).forEach(
                a -> saveBookDto.setAuthorsIds(
                        (saveBookDto.getAuthorsIds() == null ? "" : saveBookDto.getAuthorsIds() + ",") + a.getId()));
        return saveBookDto;
    }
}
