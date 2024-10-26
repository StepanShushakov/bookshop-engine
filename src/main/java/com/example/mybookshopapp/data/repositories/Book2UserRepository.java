package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author karl
 */

@Repository
public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {

    Book2UserEntity findBook2UserEntitiesByBookIdAndUserId(int bookId, int userId);

    @Query(nativeQuery = true, value = """
            select
                count(bu.id)
            from book2user bu
                join book2user_type but
                    on bu.type_id = but.id
                           and bu.user_id = :userId
                           and but.code in (:typeCodes)
""")
    int countOnUserIdAndTypeCode(@Param("userId") int userId, @Param("typeCodes") String[] typeCodes);

    @Modifying
    @Query(nativeQuery = true, value = """
            delete
            from
                book2user bu
            where
                bu.user_id = :userId and bu.book_id = :bookId
""")
    void deleteBook2UserEntityByUserIdAndBookSlug(@Param("userId") int userId,
                                                  @Param("bookId") int bookId);

    @Query(nativeQuery = true, value = """
            select
                t.code
            from book2user_type t
            join
                book2user bu
                    on bu.type_id = t.id
                        and bu.book_id = :bookId
                        and bu.user_id = :userId
                        
""")
    String findCodeTypeByUserIdAndBookId(@Param("bookId") int bookId,
                                         @Param("userId") int userId);
}
