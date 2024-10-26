package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.dto.requests.SaveAuthorDto;
import com.example.mybookshopapp.data.model.author.AuthorEntity;
import com.example.mybookshopapp.data.repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author karl
 */

@Service
public class AuthorService {

    AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Map<String, List<AuthorEntity>> getAuthorsMap() {
        List<AuthorEntity> authors = authorRepository.findAll();
        return authors.stream().collect(Collectors.groupingBy((AuthorEntity a) -> a.getName().substring(0,1)));
    }

    public AuthorEntity getAuthorEntityBySlug(String slug) {
        return authorRepository.findBySlug(slug);
    }

    public AuthorEntity getAuthorEntityById(Integer id) {
        return authorRepository.findAuthorEntityById(id);
    }

    public void save(AuthorEntity authorEntity) {
        authorRepository.save(authorEntity);
    }

    public void save(SaveAuthorDto saveAuthorDto) {
        AuthorEntity authorEntity;
        if (saveAuthorDto.getId() == 0) {
            authorEntity = new AuthorEntity();
        } else {
            authorEntity = getAuthorEntityById(saveAuthorDto.getId());
        }
        authorEntity.setSlug(saveAuthorDto.getSlug());
        authorEntity.setName(saveAuthorDto.getName());
        authorEntity.setDescription(saveAuthorDto.getDescription());
        authorRepository.save(authorEntity);
    }

    public List<AuthorEntity> getAuthorEntitiesByName(String name) {
        return authorRepository.findAuthorEntitiesByName(name);
    }
}
