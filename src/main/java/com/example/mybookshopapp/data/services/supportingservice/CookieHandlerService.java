package com.example.mybookshopapp.data.services.supportingservice;

import com.example.mybookshopapp.data.repositories.Book2UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author karl
 */

@Service
public class CookieHandlerService {

    private final Book2UserRepository book2UserRepository;

    @Autowired
    public CookieHandlerService(Book2UserRepository book2UserRepository) {
        this.book2UserRepository = book2UserRepository;
    }

    public List<Integer> getBookIdsFromContents(String cookieContents) {

        if (cookieContents == null || cookieContents.isEmpty()) {
            return null;
        }

        cookieContents = cookieContents.startsWith("/") ? cookieContents.substring(1) : cookieContents;
        cookieContents = cookieContents.endsWith("/") ? cookieContents.substring(0, cookieContents.length() - 1) :
                cookieContents;
        String[] stringBookIds = cookieContents.split("/");
        List<Integer> bookIds = new ArrayList<>();
        for (String stringBookId : stringBookIds) {
            bookIds.add(Integer.parseInt(stringBookId.replaceAll("-", "")));
        }
        return bookIds;
    }

    public int getBookCountFromDataBase(int userId, String[] types) {
        return  book2UserRepository.countOnUserIdAndTypeCode(userId, types);
    }
}
