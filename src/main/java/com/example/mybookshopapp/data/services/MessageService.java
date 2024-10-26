package com.example.mybookshopapp.data.services;

import com.example.mybookshopapp.data.dto.requests.MessageRequestDto;
import com.example.mybookshopapp.data.model.book.review.MessageEntity;
import com.example.mybookshopapp.data.model.user.UserEntity;
import com.example.mybookshopapp.data.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author karl
 */

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void addMessage(MessageRequestDto messageRequest, Object user) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setTime(LocalDateTime.now());
        if (user instanceof UserEntity userEntity) {
            messageEntity.setUser(userEntity);
        }
        messageEntity.setName(messageRequest.getName());
        messageEntity.setEmail(messageRequest.getMail());
        messageEntity.setSubject(messageRequest.getTopic());
        messageEntity.setText(messageRequest.getMessage());
        messageRepository.save(messageEntity);
    }
}
