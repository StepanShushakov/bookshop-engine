package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.dto.requests.SaveBookDto;
import com.example.mybookshopapp.data.model.book.BookEntity;
import com.example.mybookshopapp.data.model.book.links.Book2AuthorEntity;
import com.example.mybookshopapp.data.repositories.*;
import com.example.mybookshopapp.data.model.book.links.LastViewEntity;
import com.example.mybookshopapp.data.model.book.review.BookReviewEntity;
import com.example.mybookshopapp.data.model.google.api.books.Item;
import com.example.mybookshopapp.data.model.google.api.books.Root;
import com.example.mybookshopapp.data.model.user.UserEntity;
import com.example.mybookshopapp.errs.BookstoreApiWrongParameterException;
import com.example.mybookshopapp.errs.ParseDateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author karl
 */

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookReviewRepository bookReviewRepository;
    private final RestTemplate restTemplate;
    private final LastViewRepository lastViewRepository;
    private final Book2AuthorRepository book2AuthorRepository;

    @Autowired
    public BookService(BookRepository bookRepository,
                       BookReviewRepository bookReviewRepository,
                       RestTemplate restTemplate,
                       LastViewRepository lastViewRepository,
                       Book2AuthorRepository book2AuthorRepository) {
        this.bookRepository = bookRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.restTemplate = restTemplate;
        this.lastViewRepository = lastViewRepository;
        this.book2AuthorRepository = book2AuthorRepository;
    }

    //NEW BOOK SERVICE METHODS

    public List<BookEntity> getBooksEntitiesByAuthors(String author) {
        return bookRepository.findBookEntitiesByAuthorsContaining(author);
    }

    public List<BookEntity> getBookEntitiesByTitle(String title) throws BookstoreApiWrongParameterException {
//        return bookRepository.findBookEntitiesByTitleContaining(title);
        if (title.length() <= 1) {
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            List<BookEntity> data = bookRepository.findBookEntitiesByTitleContaining(title);
            if (!data.isEmpty()) {
                return data;
            } else {
                throw new BookstoreApiWrongParameterException("No data found with specified parameters...");
            }
        }
    }

    public List<BookEntity> getBookEntitiesWithPriceBetween(Integer min, Integer max) {
        return bookRepository.findBookEntitiesByPriceBetween(min, max);
    }

    public List<BookEntity> getBookEntitiesByPrice(Integer price) {
        return bookRepository.findBookEntitiesByPrice(price);
    }

    public List<BookEntity> getBestsellers() {
        return bookRepository.getBestsellers();
    }

    public List<BookEntity> getBookEntitiesWithMaxDiscount() {
        return bookRepository.getBooksEntityWithMaxDiscount();
    }

    public Page<BookEntity> getPageOfRecommendedBooks(int userId, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        if (userId == 0) {
            return bookRepository.findCommonRecommendedBooks(
                    LocalDateTime.now()
                            .withDayOfMonth(1)
                            .withHour(0)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0),
                    nextPage);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -2);
            return bookRepository.findUserRecommendedBooks(
                    userId,
                    LocalDateTime.now()
                            .withDayOfMonth(1)
                            .withHour(0)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0),
                    calendar.getTime(),
                    nextPage);
        }
    }

    public Page<BookEntity> getPageOfRecentBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findRecent(nextPage);
    }

    public Page<BookEntity> getPageOfUserViewedBooks(int userId, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBookEntitiesByLastUserView(userId, nextPage);
    }

    public Page<BookEntity> getPageOfRecentBooks(String from, String to, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return  bookRepository.findBookEntitiesByPubDateBetween(convertStringToLocalDate(from),
                convertStringToLocalDate(to),
                nextPage);
    }

    private LocalDate convertStringToLocalDate(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(string, formatter);
    }

    public Page<BookEntity> getPageOfRecentBooks(LocalDate from, LocalDate to, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBookEntitiesByPubDateBetween(from, to, nextPage);
    }

    public Page<BookEntity> getPageOfSearchResultBooks(String searchWord, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBookEntitiesByTitleContaining(searchWord, nextPage);
    }

    public Page<BookEntity> getPageOfBooksByTagName(String tagName, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBookEntitiesByTagName(tagName, nextPage);
    }

    public Page<BookEntity> getPageOfGenreSlug(String slug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBookEntityGenreBySlug(slug, nextPage);
    }

    public Page<BookEntity> getPageOfBooksByAuthorId(Integer authorId, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBookEntityByAuthorId(authorId, nextPage);
    }

    public BookEntity getBookEntityBySlug(String slug) {
        return bookRepository.findBookEntityGenreBySlug(slug);
    }

    public void save(BookEntity bookEntity) {
        bookRepository.save(bookEntity);
    }

    @Transactional
    public int save(SaveBookDto saveBookDto) throws ParseDateException {
        BookEntity bookEntity;
        if (saveBookDto.getId() == 0) {
            bookEntity = new BookEntity();
        } else {
            bookEntity = getBookEntityById(saveBookDto.getId());
        }
        bookEntity.setSlug(saveBookDto.getSlug());
        bookEntity.setTitle(saveBookDto.getTitle());
        bookEntity.setDescription(saveBookDto.getDescription());
        bookEntity.setPubDate(parsStringDate(saveBookDto.getPubDate()));
        bookEntity.setIsBestseller(saveBookDto.getIsBestseller());
        bookEntity.setPrice(saveBookDto.getPrice());
        bookEntity.setDiscount(saveBookDto.getDiscount());
        bookRepository.save(bookEntity);
        book2AuthorRepository.deleteAllByBookId(bookEntity.getId());
        String[] authorsIds = saveBookDto.getAuthorsIds().split(",");
        for (int i = 0; i < authorsIds.length; i++) {
            Book2AuthorEntity book2AuthorEntity = new Book2AuthorEntity();
            book2AuthorEntity.setBookId(bookEntity.getId());
            book2AuthorEntity.setAuthorId(Integer.parseInt(authorsIds[i]));
            book2AuthorEntity.setSortIndex(i);
            book2AuthorRepository.save(book2AuthorEntity);
        }
        return bookEntity.getId();
    }

    public List<BookEntity> getBookEntitiesByIdIn(List<Integer> bookIds) {
        return bookRepository.findBookEntitiesByIdIn(bookIds);
    }

    public void addReview(UserEntity userEntity, int bookId, String text) {
        BookReviewEntity bookReviewEntity = new BookReviewEntity();
        bookReviewEntity.setUser(userEntity);
        bookReviewEntity.setBook((BookEntity) bookRepository.findBookEntityById(bookId));
        bookReviewEntity.setTime(LocalDateTime.now());
        bookReviewEntity.setText(text);
        bookReviewRepository.save(bookReviewEntity);
    }

    @Value("${google.books.api.key}")
    private String apiKey;

    public List<BookEntity> getPageOfGoogleBooksApiSearchResult(String searchWord, Integer offset, Integer limit) {
        String REQUEST_URL = "https://www.googleapis.com/books/v1/volumes" +
                "?q=" + searchWord +
                "&key=" + apiKey +
                "&filter=paid-ebooks" +
                "&startIndex=0" + offset +
                "&maxResults=" + limit;

        Root root = restTemplate.getForEntity(REQUEST_URL, Root.class).getBody();
        ArrayList<BookEntity> list = new ArrayList<>();
        if (root != null) {
            for (Item item : root.getItems()) {
                BookEntity bookEntity = getBookEntity(item);
                list.add(bookEntity);
            }
        }
        return list;
    }

    private static BookEntity getBookEntity(Item item) {
        BookEntity bookEntity = new BookEntity();
        if (item.getVolumeInfo() != null) {
            //bookEntity.setAuthors((List<AuthorEntity>) new AuthorEntity(item.getVolumeInfo().getAuthors()));
            bookEntity.setTitle(item.getVolumeInfo().getTitle());
            bookEntity.setImage(item.getVolumeInfo().getImageLinks().getThumbnail());
        }
        if (item.getSaleInfo() != null) {
            bookEntity.setPrice((int) item.getSaleInfo().getRetailPrice().getAmount());
            double oldPrice = item.getSaleInfo().getListPrice().getAmount();
            bookEntity.setDiscount((short) (bookEntity.getPrice() / oldPrice));
        }
        return bookEntity;
    }

    public void saveLastViewTime(Integer userId, Integer bookId) {
        LastViewEntity lastView = lastViewRepository.findLastBooksEntityByUserIdAndBookId(userId, bookId);
        if (lastView == null) {
            lastView = new LastViewEntity();
            lastView.setUserId(userId);
            lastView.setBookId(bookId);
        }
        lastView.setDateTime(LocalDateTime.now());
        lastViewRepository.save(lastView);
    }

    public void addDaysToPubDate(int days) {
        List<BookEntity> books = bookRepository.findAll();
        Calendar c = Calendar.getInstance();
        for (BookEntity book: books) {
            Date date = book.getPubDate();
            c.setTime(date);
            c.add(Calendar.DATE, days);
            book.setPubDate(c.getTime());
            bookRepository.save(book);
        }
    }

    public List<BookEntity> getBookEntitiesByUserIdAndType(int userId, String type) {
        return bookRepository.findBookEntitiesByUserIdAndTypeCode(userId, type);
    }

    public List<String> getTagsOnBookId(Integer bookId) {
        return bookRepository.findTagsOnBookId(bookId);
    }

    public List<Integer> getBookIdsBuUserIdAndTypeCode(int userId, String typeCode) {
        return bookRepository.findBookIdsByUserIdAndTypeCode(userId, typeCode);
    }

    public void deleteBookById(int id) {
        bookRepository.deleteById(id);
    }

    public BookEntity getBookEntityById(int id) {
        return bookRepository.getById(id);
    }

    private Date parsStringDate(String pubDate) throws ParseDateException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return formatter.parse(pubDate);
        } catch (ParseException e) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return formatter.parse(pubDate);
            } catch (ParseException ex) {
                throw new ParseDateException("wrong date format");
            }
        }
    }
}
