package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.author.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author karl
 */

public interface AuthorRepository extends JpaRepository<AuthorEntity,Integer> {
    @Query("select a from AuthorEntity a where a.slug = ?1")
    AuthorEntity findBySlug(String slug);

    @Query(nativeQuery = true, value = """
            select
                concat(a.name, case when ai.notAlone then concat(' ', :moreString) else '' end) as name
            from book2author as b2a
                inner join author as a
                    on b2a.author_id = a.id
                           and b2a.book_id = :book_id
                inner join (select
                                min(b2a.sort_index) as first_author_index,
                                count(b2a.id) > 1 as notAlone
                            from book2author as b2a
                                inner join author as a
                                    on b2a.author_id = a.id
                                           and b2a.book_id = :book_id) as ai
                    on b2a.sort_index = ai.first_author_index
""")
    String getAuthorsStringOnBookId(@Param("book_id") Integer id, @Param("moreString") String moreString);

    @Query(nativeQuery = true, value = """
            select
                a.*
            from
                book2author ba
                    join author a
                        on ba.author_id = a.id
                            and ba.book_id = :bookId
            order by
                ba.sort_index
""")
    List<AuthorEntity> findAuthorEntitiesByBook(@Param("bookId") int bookId);

    @Query("select a from AuthorEntity a where a.Id = ?1")
    AuthorEntity findAuthorEntityById(Integer id);

    @Query(nativeQuery = true,
            value = "select a.* from author a where a.name like concat('%', :name , '%') limit 1",
            countQuery = "select 1")
    AuthorEntity findAuthorEntityByName(@Param("name") String name);

    @Query(nativeQuery = true,
            value = "select a.* from author a where a.name like concat('%', :name , '%')")
    List<AuthorEntity> findAuthorEntitiesByName(@Param("name") String name);
}
