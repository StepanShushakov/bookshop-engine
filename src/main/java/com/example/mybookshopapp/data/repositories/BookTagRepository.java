package com.example.mybookshopapp.data.repositories;

import com.example.mybookshopapp.data.model.book.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author karl
 */

public interface BookTagRepository extends JpaRepository<TagEntity, Integer> {

    @Query(nativeQuery = true, value = """
            select
                t.*
            from
                tag t
            join
                book2tag bt
                    on t.id = bt.tag_id
                        and bt.book_id = :bookId
""")
    List<TagEntity> findTagsOnBookId(@Param("bookId") int bookId);
}
