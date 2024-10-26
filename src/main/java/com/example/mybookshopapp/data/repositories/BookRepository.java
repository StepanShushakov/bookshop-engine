package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.book.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author karl
 */

public interface BookRepository extends JpaRepository<BookEntity, Integer> {

//    List<BookEntity> findBooksByAuthor_FirstName(String name);

    @Query("from BookEntity")
    List<BookEntity> customFindAllBooks();

    //NEW BOOK REST REPOSITORY COMMANDS

    @Query(nativeQuery = true, value = "select b.* "
            + "from book b inner join book2author b2a on b.id = b2a.book_id "
            + "inner join author a on b2a.author_id = a.id and a.name like '%' || :authorName || '%'")
    List<BookEntity> findBookEntitiesByAuthorsContaining(@Param("authorName") String author);

    List<BookEntity> findBookEntitiesByTitleContaining(String bookTitle);

    List<BookEntity> findBookEntitiesByPriceBetween(Integer min, Integer max);

    List<BookEntity> findBookEntitiesByPrice(Integer price);

    @Query("from BookEntity where isBestseller=1")
    List<BookEntity> getBestsellers();

    @Query(value = "select * from book where discount = (select max(discount) from book)", nativeQuery = true)
    List<BookEntity> getBooksEntityWithMaxDiscount();

    Page<BookEntity> findBookEntitiesByTitleContaining(String bookTitle, Pageable nextPage);

    @Query(nativeQuery = true, value = "select b.* from book b order by b.pub_date desc")
    Page<BookEntity> findRecent(Pageable nextPage);

    @Query(nativeQuery = true, value = """
            select b.*
              from book b
              left join book2user ub
                join book2user_type tb
                on ub.type_id = tb.id
                and tb.code = 'PAID'
              on ub.book_id = b.id
              left join book2user uc
                join book2user_type tc
                on uc.type_id = tc.id
                and tc.code = 'CART'
              on uc.book_id = b.id
              left join book2user uk
                join book2user_type tk
                on uk.type_id = tk.id
                and tk.code = 'KEPT'
              on uk.book_id = b.id
              left join last_books lb
              on lb.book_id = b.id
              and lb.date_time >= :afterTime
            group by b.id
            order by count(distinct ub.id) + count(distinct uc.id) * 0.7 + count(distinct uk.id) * 0.4 + 0.2 * count(distinct lb.id) desc""")
    Page<BookEntity> findPopular(@Param("afterTime") LocalDateTime after, Pageable nextPage);

    @Query(nativeQuery = true, value = "select * from book "
            + "where pub_date between :from and :to "
            + "order by pub_date desc")
    Page<BookEntity> findBookEntitiesByPubDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to, Pageable nextPage);

    @Query(nativeQuery = true, value = "select t.name" +
            "   from tag t" +
            "       inner join book2tag b2t" +
            "           inner join book b" +
            "           on b2t.book_id = b.id" +
            "           and b.id = :book_id" +
            "       on t.id = b2t.tag_id")
    List<String> findTagsOnBookId(@Param("book_id") Integer bookId);

    @Query(nativeQuery = true, value = "select b.*" +
            "   from book b" +
            "       inner join book2tag b2t" +
            "           inner join tag t" +
            "           on b2t.tag_id = t.id" +
            "           and t.name = :name" +
            "       on b.id = b2t.book_id " +
            "order by" +
            "   b.pub_date desc ")
    Page<BookEntity> findBookEntitiesByTagName(@Param("name") String tagName, Pageable nextPage);

    @Query(nativeQuery = true, value = "select b.* " +
            "from book b" +
            "   inner join book2genre b2g" +
            "       inner join genre g" +
            "       on b2g.genre_id = g.id" +
            "       and g.slug = :slug" +
            "   on b.id = b2g.book_id " +
            "order by" +
            "   b.pub_date desc")
    Page<BookEntity> findBookEntityGenreBySlug(String slug, Pageable nextPage);

    @Query(nativeQuery = true, value = "select b.* " +
            "from book b" +
            "   inner join book2author b2a" +
            "   on b.id = b2a.book_id" +
            "   and b2a.author_id = :author_id " +
            "order by b.pub_date desc")
    Page<BookEntity> findBookEntityByAuthorId(@Param("author_id") Integer author_id, Pageable nextPage);

    BookEntity findBookEntityGenreBySlug(String slug);

    List<BookEntity> findBookEntitiesByIdIn(List<Integer> id);

    Object findBookEntityById(int bookId);

    @Query(nativeQuery = true, value = """
            select
                b.id, b.pub_date, b.is_bestseller, b.slug, b.title, b.image, b.description, b.price, b.discount
            from
                (select
                     b.*,
                     (select coalesce(round(avg(br.value)), 0) from book_rating br where br.book_id = b.id) rating,
                     (select count(distinct lb.id) from last_books lb where lb.book_id = b.id and lb.date_time >= :after) views
                 from book b) b
            order by
                0.1 * b.rating + 0.5 * b.views desc
""", countQuery = "select count(id) from book")
    Page<BookEntity> findCommonRecommendedBooks(@Param("after") LocalDateTime after, Pageable nextPage);

    @Query(nativeQuery = true, value = """
            select
                b.id, b.pub_date, b.is_bestseller, b.slug, b.title, b.image, b.description, b.price, b.discount
            from
                (select
                     b.*,
                     (select
                          coalesce(round(avg(value)), 0)
                      from
                          book_rating br
                      where
                              br.book_id = b.id) as rating,
                     (select count(*) from book b2 where b2.id = b.id and b2.pub_date >= :afterPub)  as new,
                     (select
                          count(distinct bg.genre_id)
                      from
                          book2genre bg, book2genre bg_other, last_books lb
                      where
                          lb.user_id = :userId and lb.book_id = bg.book_id and
                          lb.date_time >= :afterView and bg.genre_id = bg_other.genre_id and
                          b.id = bg_other.book_id) as genre_views,
                     (select
                          count(distinct ba.author_id)
                      from
                          book2author ba, book2author ba_other, last_books lb
                      where
                          lb.user_id = :userId and lb.book_id = ba.book_id and
                          lb.date_time >= :afterView and ba.author_id = ba_other.author_id and
                          lb.book_id != b.id and
                          b.id = ba_other.book_id) as author_views,
                     (select
                          count(distinct bt.tag_id)
                      from
                          book2tag bt, book2tag bt_other, last_books lb
                      where
                          lb.user_id = :userId and lb.book_id = bt.book_id and
                          lb.date_time >= :afterView and bt.tag_id = bt_other.tag_id and
                          lb.book_id != b.id and
                          b.id = bt_other.book_id) as tag_views,
                     (select
                          count(*)
                      from
                          book2user bu, book2user_type ut, book2author ba, book2author ba_other
                      where
                          bu.user_id = :userId and bu.type_id = ut.id and ut.code = 'PAID' and
                          bu.book_id = ba.book_id and
                          ba.author_id = ba_other.author_id and ba.book_id != ba_other.book_id and
                          b.id = ba_other.book_id) as cnt_author_paid,
                     (select
                          count(*)
                      from
                          book2user bu, book2user_type ut, book2genre bg, book2genre bg_other
                      where
                          bu.user_id = :userId and bu.type_id = ut.id and ut.code = 'PAID' and
                          bu.book_id = bg.book_id and
                          bg.genre_id = bg_other.genre_id and bg.book_id != bg_other.book_id and
                          b.id = bg_other.book_id) as cnt_genre_paid,
                     (select
                          count(*)
                      from book2user bu, book2user_type ut ,book2tag bt, book2tag bt_other
                      where
                          bu.user_id = :userId and bu.type_id = ut.id and ut.code = 'PAID' and
                          bu.book_id = bt.book_id and
                          bt.tag_id = bt_other.tag_id and bt.book_id != bt_other.book_id and
                          b.id = bt_other.book_id) as cnt_tag_paid,
                      (select
                          count(*)
                      from
                          book2user bu, book2user_type ut, book2author ba, book2author ba_other
                      where
                          bu.user_id = :userId and bu.type_id = ut.id and ut.code = 'CART' and
                          bu.book_id = ba.book_id and
                          ba.author_id = ba_other.author_id and ba.book_id != ba_other.book_id and
                          b.id = ba_other.book_id) as cnt_author_cart,
                     (select
                          count(*)
                      from
                          book2user bu, book2user_type ut, book2genre bg, book2genre bg_other
                      where
                          bu.user_id = :userId and bu.type_id = ut.id and ut.code = 'CART' and
                          bu.book_id = bg.book_id and
                          bg.genre_id = bg_other.genre_id and bg.book_id != bg_other.book_id and
                          b.id = bg_other.book_id) as cnt_genre_cart,
                     (select
                          count(*)
                      from book2user bu, book2user_type ut ,book2tag bt, book2tag bt_other
                      where
                          bu.user_id = :userId and bu.type_id = ut.id and ut.code = 'CART' and
                          bu.book_id = bt.book_id and
                          bt.tag_id = bt_other.tag_id and bt.book_id != bt_other.book_id and
                          b.id = bt_other.book_id) as cnt_tag_cart,
                      (select
                          count(*)
                      from
                          book2user bu, book2user_type ut, book2author ba, book2author ba_other
                      where
                          bu.user_id = :userId and bu.type_id = ut.id and ut.code = 'KEPT' and
                          bu.book_id = ba.book_id and
                          ba.author_id = ba_other.author_id and ba.book_id != ba_other.book_id and
                          b.id = ba_other.book_id) as cnt_author_kept,
                     (select
                          count(*)
                      from
                          book2user bu, book2user_type ut, book2genre bg, book2genre bg_other
                      where
                          bu.user_id = :userId and bu.type_id = ut.id and ut.code = 'KEPT' and
                          bu.book_id = bg.book_id and
                          bg.genre_id = bg_other.genre_id and bg.book_id != bg_other.book_id and
                          b.id = bg_other.book_id) as cnt_genre_kept,
                     (select
                          count(*)
                      from book2user bu, book2user_type ut ,book2tag bt, book2tag bt_other
                      where
                          bu.user_id = :userId and bu.type_id = ut.id and ut.code = 'KEPT' and
                          bu.book_id = bt.book_id and
                          bt.tag_id = bt_other.tag_id and bt.book_id != bt_other.book_id and
                          b.id = bt_other.book_id) as cnt_tag_kept
                 from book b) b
            order by
                new +
                0.9 * rating +
                0.85 * cnt_author_paid +
                0.8 * cnt_genre_paid +
                0.75 * cnt_tag_paid +
                0.7 * cnt_author_cart +
                0.65 * cnt_genre_cart +
                0.6 * cnt_tag_cart +
                0.55 * cnt_author_kept +
                0.5 * cnt_genre_kept +
                0.45 * cnt_tag_kept +
                0.4 * author_views +
                0.3 * genre_views +
                0.2 * tag_views desc
            """, countQuery = "select count(id) from book")
    Page<BookEntity> findUserRecommendedBooks(@Param("userId") int userId,
                                              @Param("afterView") LocalDateTime afterView,
                                              @Param("afterPub") Date afterPub,
                                              Pageable nextPage);

    @Query(nativeQuery = true, value = """
            select
                b.*
            from
                book b
            join last_books lb
            on b.id = lb.book_id
            where
                lb.user_id = :userId
            order by
                lb.date_time desc
""")
    Page<BookEntity> findBookEntitiesByLastUserView(@Param("userId") int userId, Pageable nextPage);

    @Query(nativeQuery = true, value = """
            select
                b.*
            from book b
            join book2user bu
                join book2user_type but
                    on bu.type_id = but.id and but.code = :typeCode
                on b.id = bu.book_id and bu.user_id = :userId
""")
    List<BookEntity> findBookEntitiesByUserIdAndTypeCode(@Param("userId") int userId, @Param("typeCode") String typeCode);

    @Query(nativeQuery = true, value = """
            select
                bu.id
            from
                book2user bu
                join book2user_type but
                    on bu.type_id = but.id
                        and bu.user_id = :userId
                        and but.code = :typeCode
""")
    List<Integer> findBookIdsByUserIdAndTypeCode(@Param("userId") int userId,
                                                         @Param("typeCode") String typeCode);
}
