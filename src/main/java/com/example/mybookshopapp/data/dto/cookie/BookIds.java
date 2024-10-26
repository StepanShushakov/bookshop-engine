package com.example.mybookshopapp.data.dto.cookie;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author karl
 */

@Getter
@Setter
public class BookIds {

    private List<Integer> cartIds;
    private List<Integer> postponedIds;

    public BookIds() {
    }

    public String getAllPostponedIds() {
        StringJoiner sj = new StringJoiner(", ");
        postponedIds.forEach(postponedId -> sj.add(postponedId.toString()));
        return "[" + sj + "]";
    }
}
